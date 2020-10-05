/*
 * Copyright 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.speech;

import com.example.speech.ErrorRate.Alignment;
// [START speech_quickstart]
// Imports the Google Cloud client library
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class QuickstartSample {
	private static boolean DEBUG = true;

	/** Demonstrates using the Speech API to transcribe an audio file. */
	public static void main(String... args) throws Exception {
		// Instantiates a client
		try (SpeechClient speechClient = SpeechClient.create()) {

			// initiate the CSV file
			try (PrintWriter writer = new PrintWriter(new File("Google SR Results.csv"))) {

				StringBuilder sb = new StringBuilder();
				StringBuilder sb2 = new StringBuilder();

				sb.append("id");
				sb.append(',');
				sb.append("File Name");
				sb.append(',');
				sb.append("Ground Truth Script");
				sb.append(',');
				sb.append("SR Transcript");
				sb.append(',');
				sb.append("Ground Truth Length");
				sb.append(',');
				sb.append("SR Transcript Length");
				sb.append(',');
				sb.append("# correct tokens");
				sb.append(',');
				sb.append("# Substitutions");
				sb.append(',');
				sb.append("# Insertions");
				sb.append(',');
				sb.append("# Deletions");
				sb.append(',');
				sb.append("Accuracy");
				sb.append(',');
				sb.append("WER");
				sb.append('\n');
				writer.write(sb.toString());

				// The path to the audio file to transcribe
				String FileName = "";
				@SuppressWarnings("unused")
				int sequence = 1;
				RecognitionConfig config = null;
				RecognitionAudio audio;
				File pathdir = new File("./resources/");
				File[] files = pathdir.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile() && !files[i].getName().equals(".DS_Store")) { // this line weeds out other
																						// directories/folders

						FileName = files[i].getName();
						System.out.println(files.length);
						int dot = FileName.indexOf(".");
						String ext = FileName.substring(dot, FileName.length());

						System.out.println(sequence + "/ " + FileName + "/" + ext);
						String audioPath = "./resources/" + FileName;

						// Reads the audio file into memory
						Path path = Paths.get(audioPath);
						byte[] data = Files.readAllBytes(path);
						ByteString audioBytes = ByteString.copyFrom(data);

						// Builds the sync recognize request

						if (ext.equals(".flac")) {

							config = RecognitionConfig.newBuilder().setAudioChannelCount(2)
									.setEncoding(AudioEncoding.FLAC).setLanguageCode("en-US").build();
						} else if (ext.equals(".raw")) {
							config = RecognitionConfig.newBuilder().setAudioChannelCount(2)
									.setEncoding(AudioEncoding.LINEAR16).setSampleRateHertz(16000)
									.setLanguageCode("en-US").build();

						} else if (ext.equals(".mp3")) {
							config = RecognitionConfig.newBuilder().setAudioChannelCount(2)
									.setEncoding(AudioEncoding.LINEAR16).setSampleRateHertz(16000)
									.setLanguageCode("en-US").build();

						} else {
							config = RecognitionConfig.newBuilder().setAudioChannelCount(2)
									.setEncoding(AudioEncoding.LINEAR16).setLanguageCode("en-US").build();

						}

						audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

						// Performs speech recognition on the audio file
						RecognizeResponse response = speechClient.recognize(config, audio);
						List<SpeechRecognitionResult> results = response.getResultsList();
						String SRRESULT = "";
						// this line can be changed to a list of original transcriptions
						String reference = "Please call Stella Ask her to bring these things with her from the store Six spoons of fresh snow peas five thick slabs of blue cheese and maybe a snack for her brother Bob We also need a small plastic snake and a big toy frog for the kids She can scoop these things into three red bags and we will go meet her Wednesday at the train station";
						for (SpeechRecognitionResult result : results) {
							// There can be several alternative transcripts for a given chunk of speech.
							// Just use the
							// first (most likely) one here.
							SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
							// System.out.printf("Transcription: %s%n", alternative.getTranscript());
							SRRESULT = SRRESULT + alternative.getTranscript();
						}

						System.out.printf("Transcription for " + FileName + ": %s%n", SRRESULT);

						ErrorRate werEval = new ErrorRate();
						String[] hyp = SRRESULT.split(" ");
						String[] ref = reference.split(" ");
						Alignment a = werEval.align(ref, hyp);

						System.out.println(a);

						// adding results to CSV files
						sb2.append(sequence++);
						sb2.append(',');
						sb2.append(FileName);
						sb2.append(',');
						sb2.append(reference);
						sb2.append(',');
						sb2.append(SRRESULT);
						sb2.append(',');
						sb2.append(a.getReferenceLength());
						sb2.append(',');
						sb2.append(a.getHypothesisLength());
						sb2.append(',');
						sb2.append(a.getNumCorrect());
						sb2.append(',');
						sb2.append(a.numSubstitutions);
						sb2.append(',');
						sb2.append(a.numInsertions);
						sb2.append(',');
						sb2.append(a.numDeletions);
						sb2.append(',');
						sb2.append((int) ((a.getNumCorrect() / (float) a.getReferenceLength()) * 100) + "%");
						sb2.append(',');
						sb2.append(((a.numSubstitutions + a.numInsertions + a.numDeletions)
								/ (float) a.getReferenceLength()));
						sb2.append('\n');

						writer.write(sb2.toString());
						sb2.delete(0, sb2.length());

					} // end if

				} // end for loop
				System.out.println("Writing to CSV is done!");

			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}

	}

}