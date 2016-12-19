package edu.uffs.storminho.bolts;


import edu.uffs.storminho.SharedMethods;
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
    double precision, revocation;

    @Override
    public void prepare(Map map, TopologyContext context, OutputCollector collector) {
        _collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        precision = tuple.getDouble(0);
        revocation = tuple.getDouble(1);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("", ""));
    }

    @Override
    public void cleanup() {
        SharedMethods.printForResultsFileln("Precisão: " + precision + " Revocação: " + revocation);
    }
}
