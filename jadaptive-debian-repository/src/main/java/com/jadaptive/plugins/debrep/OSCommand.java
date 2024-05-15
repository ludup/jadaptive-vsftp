package com.jadaptive.plugins.debrep;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSCommand {

	static Logger LOG = LoggerFactory.getLogger(OSCommand.class);

	public static void runAndCheckExit(Object... arguments) {
		LOG.info("Running: {}", String.join(" ", Arrays.asList(arguments).stream().map(Object::toString).toList()));
		var pb = new ProcessBuilder(Arrays.asList(arguments).stream().map(Object::toString).toList());
		pb.redirectError(Redirect.INHERIT);
		pb.redirectInput(Redirect.INHERIT);
		try {
			var prc = pb.start();
			var res = prc.waitFor();
			if(res != 0) {
				throw new IOException("Unexpected exit code " + res);
			}
		}
		catch(IOException ioe) {
			throw new UncheckedIOException(ioe);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static String runCommandAndCaptureOutput(Object... arguments) {
		LOG.info("Running: {}", String.join(" ", Arrays.asList(arguments).stream().map(Object::toString).toList()));
		var pb = new ProcessBuilder(Arrays.asList(arguments).stream().map(Object::toString).toList());
		pb.redirectError(Redirect.INHERIT);
		try {
			var prc = pb.start();
			var wtr = new StringWriter();
			try(var in = new InputStreamReader(prc.getInputStream(), "UTF-8")) {
				in.transferTo(wtr);
			}
			return wtr.toString();
		}
		catch(IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

}
