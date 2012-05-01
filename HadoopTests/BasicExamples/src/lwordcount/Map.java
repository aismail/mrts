package lwordcount;


import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map extends Mapper<Text, Text, Text, IntWritable> {
	public Text nkey = new Text();
	
	public void map(Text key, Text value, Context context) 
		throws IOException, InterruptedException {
		nkey.set(key);
		context.write(nkey, new IntWritable(value.toString().split(" ").length));
	}
}
