package com.jadaptive.plugins.debrep;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;

public class OSCommand {

	public static void runAndCheckExit(Object... arguments) {
		var pb = new ProcessBuilder(Arrays.asList(arguments).stream().map(Object::toString).toList());
		pb.redirectError(Redirect.INHERIT);
		pb.redirectInput(Redirect.INHERIT);
		try {
			var prc = pb.start();
			var res = prc.waitFor();
			if(res != -1) {
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
