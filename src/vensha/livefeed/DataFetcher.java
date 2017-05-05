package vensha.livefeed;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vensha.livefeed.utils.BlockingQueue;
import vensha.livefeed.utils.LogManager;
import vensha.livefeed.utils.ProcessListener;
import vensha.livefeed.utils.Processor;

public class DataFetcher implements ProcessListener {
@SuppressWarnings("rawtypes")
private BlockingQueue queue_ = new BlockingQueue();
private boolean error_ = false;
private boolean allStepsCompleted_ = false;
private StringBuilder output_ = new StringBuilder();
private static String FILE_MARKER = "[ContentFile]";
private List<String> contentFiles_ = new ArrayList<String>();
private String fileId_ = null;

public boolean scrape(String resFile) throws Exception {
	fileId_ = new File(resFile).getName();
	output_.append("Starting casper on " + resFile);
	Processor runner = new Processor("casperjs " + resFile, this);

	runner.setShowAdditonalMessages(false);
	runner.process();
	queue_.getNext();
	if (error_)
		LogManager.getCurrentLogger().log("Fetching : " + resFile + " failed");
	else
		LogManager.getCurrentLogger().log("Fetching : " + resFile + " succeeded");

	return error_;
}

@Override
public void processStarted(Processor p) {

}

@Override
public void processMessage(String line) {
	if (line.startsWith(FILE_MARKER)) {
		contentFiles_.add(line.split(":")[1]);
	}
	output_.append(line);
	output_.append("\n");
	if ((error_ == false) && line.contains("CasperError")) {
		error_ = true;
		LogManager.getCurrentLogger().log(" Error: " + line);
	}
	if (line.contains("[phantom] Step")) {
		try {
			String[] t = line.split(" +");
			String val = t[4].substring(0, t[4].length() - 1);
			int p = val.indexOf("/");
			int stepNumber = Integer.parseInt(val.substring(0, p));
			int totalSteps = Integer.parseInt(val.substring(p + 1));
			System.out.println(stepNumber + " of " + totalSteps);
			allStepsCompleted_ = stepNumber == totalSteps;
		} catch (Throwable t) {
			allStepsCompleted_ = true; // Safety net
		}
	}
}

@Override
public void processComplete(Processor p) {

	LogManager.getCurrentLogger().log("[Fetch]: " + fileId_);
	LogManager.getCurrentLogger().log(output_.toString());

	if (contentFiles_.size() == 0) {
		// error_ = true;
		LogManager.getCurrentLogger().log(" [WARN]: No content files generated");
		output_.append("\n");
		output_.append("No content files generated");
	} else {
		if (allStepsCompleted_ == false) {
			LogManager.getCurrentLogger().log("[WARN]: Could not gather all data");
			// error_ = true;
		}
	}
	queue_.add(p);
}

public String getOutput() {
	return output_.toString();
}

public List<String> getContentFiles() {
	return contentFiles_;
}

}
