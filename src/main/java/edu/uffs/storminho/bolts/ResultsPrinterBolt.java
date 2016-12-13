package edu.uffs.storminho.bolts;


import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.task.OutputCollector;

import java.util.Map;
import org.apache.storm.task.TopologyContext;

public class ResultsPrinterBolt extends BaseRichBolt implements IRichBolt {
    OutputCollector _collector;

    @Override
    public void prepare(Map map, TopologyContext context, OutputCollector collector) {
        _collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("", ""));
    }

    @Override
    public void cleanup() {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;
        try {
            fw = new FileWriter("myfile.txt", true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.println("the text");
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
        finally {
            try {
                if(out != null)
                    out.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            try {
                if(bw != null)
                    bw.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            try {
                if(fw != null)
                    fw.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }
    }
}
