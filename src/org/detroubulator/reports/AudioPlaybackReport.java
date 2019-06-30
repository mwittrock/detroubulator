/*
 * Copyright 2006, 2007 AppliCon A/S
 * 
 * This file is part of Detroubulator.
 * 
 * Detroubulator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Detroubulator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Detroubulator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.detroubulator.reports;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.detroubulator.core.Logging;
import org.detroubulator.mappingprograms.MessageMapping;
import org.detroubulator.util.ConfigurationException;
import org.detroubulator.util.Console;

final class AudioPlaybackReport extends StatsCollector implements TestReport {

	private static final String CONSOLE_LABEL = "AudioPlaybackReport";

	private static Logger log = Logging.getLogger(MessageMapping.class);
	private static final int AUDIO_BUFFER_SIZE = 128000;
	private static final Set<String> MANDATORY_PARAMS;
	
	static {
		MANDATORY_PARAMS = new HashSet<String>();
		MANDATORY_PARAMS.add("success");
		MANDATORY_PARAMS.add("failure");
	}
	
	private AudioInputStream success;
	private AudioInputStream failure;
	private boolean configured;
	
	AudioPlaybackReport() {
		super();
		configured = false;
	}

	public void endTesting() throws ReportException {
		super.endTesting();
		if (failedAssertions > 0) {
			play(failure);
		} else {
			play(success);
		}
	}

	public boolean isConfigured() {
		return configured;
	}

	public void configure(Map<String, List<String>> params) throws ConfigurationException {
		// Make sure that all mandatory parameters are present.
		for (String mandatory : MANDATORY_PARAMS) {
			if (!params.containsKey(mandatory)) {
				throw new ConfigurationException(String.format("Mandatory parameter not present: %s", mandatory));
			}
		}
		// We're expecting each List<String> to contain at least one String.
		for (List<String> l : params.values()) {
			assert l.size() > 0;
		}
		try {
			success = getStream(params.get("success").get(0));
			failure = getStream(params.get("failure").get(0));
		} catch (IllegalArgumentException iae) {
			throw new ConfigurationException(iae.getMessage());
		}
		configured = true;
	}

	private void play(AudioInputStream audio) {
		try {
			AudioFormat audioFormat = audio.getFormat();
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(audioFormat);
			line.start();
			int bytesRead = 0;
			byte[] buff = new byte[AUDIO_BUFFER_SIZE];
			while (bytesRead != -1) {
				bytesRead = audio.read(buff, 0, buff.length);
				if (bytesRead >= 0) {
					line.write(buff, 0, bytesRead);
				}
			}
			line.drain();
			line.close();
		} catch (Exception e) {
			String msg = "Error during audio playback: " + e.getMessage();
			log.warning(msg);
			Console.startSection(CONSOLE_LABEL);
			Console.p(msg);
			Console.endSection();
		}
	}

	private AudioInputStream getStream(String fileName) throws IllegalArgumentException {
		AudioInputStream ais;
		File audioFile = new File(fileName);
		if (!audioFile.exists()) {
			throw new IllegalArgumentException("Audio file does not exist: " + fileName);
		}
		if (!audioFile.isFile()) {
			throw new IllegalArgumentException("Audio file is not a file: " + fileName);
		}
		try {
			ais = AudioSystem.getAudioInputStream(audioFile);
		} catch (Exception e) {
			throw new IllegalArgumentException("Audio file could not be converted to an audio stream: " + fileName);
		}
		return ais;
	}

}