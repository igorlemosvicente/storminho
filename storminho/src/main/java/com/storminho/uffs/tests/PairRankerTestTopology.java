package com.storminho.uffs.tests;

import com.storminho.uffs.bolts.CounterBolt;
import com.storminho.uffs.bolts.DecisionTreeBolt;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

import com.storminho.uffs.bolts.PairRankerBolt;

public class PairRankerTestTopology {

  public static void main(String[] args) throws Exception {

    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("test-spout", new PairSpout(), 1);
    builder.setBolt("pair-ranker", new PairRankerBolt(), 1).shuffleGrouping("test-spout");
//    builder.setBolt("training-creator", new TrainingCreator(), 1).shuffleGrouping("pair-ranker");
    builder.setBolt("decisiontree", new DecisionTreeBolt(), 1).shuffleGrouping("pair-ranker");
    builder.setBolt("counter", new CounterBolt(), 1).shuffleGrouping("decisiontree");


    Config conf = new Config();
    conf.setDebug(false);

    if (args != null && args.length > 0) {
      conf.setNumWorkers(3);
      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
    }
    else {
    //   conf.setMaxTaskParallelism(1);
      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("test-topology", conf, builder.createTopology());
      System.out.println("\n\n\n=================================================");
      System.out.println("Fim da Topologia.");
      System.out.println("=================================================\n\n\n");
    }
  }
}
