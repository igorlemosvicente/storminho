package edu.uffs.storminho.topologies;

import edu.uffs.storminho.bolts.LineSaverBolt;
import edu.uffs.storminho.LineSpout;
import edu.uffs.storminho.Variables;
import edu.uffs.storminho.SharedMethods;
import edu.uffs.storminho.bolts.CounterBolt;
import edu.uffs.storminho.bolts.DecisionTreeBolt;
import edu.uffs.storminho.bolts.PairGeneratorBolt;
import edu.uffs.storminho.bolts.PairRankerBolt;
import edu.uffs.storminho.bolts.SplitSentenceBolt;
import edu.uffs.storminho.bolts.TrainingCreatorBolt;
import edu.uffs.storminho.bolts.WordIndexSaveBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import redis.clients.jedis.Jedis;

public class ForTopology {
    private static final double EPS = 0.0000001;
    private static final long TIME_TO_SLEEP = 5_000L; //1_000 = 1 second

    private static TopologyBuilder basicTopologyBuilder() {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("line-spout", new LineSpout());
        builder.setBolt("line-saver", new LineSaverBolt(), 1).shuffleGrouping("line-spout");
        builder.setBolt("split-sentence", new SplitSentenceBolt(), 1).shuffleGrouping("line-spout");
        builder.setBolt("index-save", new WordIndexSaveBolt(), 1).shuffleGrouping("split-sentence");
        builder.setBolt("pair-generator", new PairGeneratorBolt(), 10).shuffleGrouping("index-save");
        builder.setBolt("pair-ranker", new PairRankerBolt(), 1).shuffleGrouping("pair-generator");
        return builder;
    }

    private static Config basicConfig() {
        Config conf = new Config();
        conf.setDebug(false);
        conf.setMaxTaskParallelism(10);
        return conf;
    }

    public static void main(String[] args) throws Exception {
        Jedis jedis = new Jedis("localhost");
        Variables.COUNT_MODE = false;

        SharedMethods.forResultsFileInit();
        SharedMethods.printForResultsFileln("Testes com o arquivo " + Variables.DATASET_INPUT);


        for (double ss = 0.1; 1 - ss > EPS; ss += 0.1) {
            Variables.SAMPLE_SIZE = ss;
            //create trainingSet
            jedis.flushAll();
            TopologyBuilder builderTrain = basicTopologyBuilder();
            builderTrain.setBolt("training-creator", new TrainingCreatorBolt(), 1).shuffleGrouping("pair-ranker");

            Config confTrain = basicConfig();
            LocalCluster clusterTrain = new LocalCluster();

            clusterTrain.submitTopology("creating-trainingset", confTrain, builderTrain.createTopology());
            SharedMethods.printStatusln("\nEnd of Training creation step\nGoing to sleep\n");
            Thread.sleep(TIME_TO_SLEEP);
            clusterTrain.shutdown();

            //print status

            SharedMethods.printForResultsFileln("Sample Size: " + ss);

            for (int i = 0; i < 10; i++) { //run test 10 times
                SharedMethods.printForResultsFile("Iteração " + i + ": ");

                TopologyBuilder builderTest = basicTopologyBuilder();
                jedis.flushAll();
                builderTest.setBolt("decision-tree", new DecisionTreeBolt(), 1).shuffleGrouping("pair-ranker");
                builderTest.setBolt("counter", new CounterBolt(), 1).shuffleGrouping("decision-tree");

                Config confTest = basicConfig();
                LocalCluster clusterTest = new LocalCluster();

                clusterTest.submitTopology("storminho-topology", confTest, builderTest.createTopology());

                //print status
                SharedMethods.printStatusln("\nEnd of iteration " + i + " of SAMPLE_SIZE " + ss + "\nDormindo agora...\n");

                Thread.sleep(TIME_TO_SLEEP);
                clusterTest.shutdown();
            }
        }
    }
}
