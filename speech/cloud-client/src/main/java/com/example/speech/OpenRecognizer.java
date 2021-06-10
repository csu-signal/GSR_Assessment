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

public class OpenRecognizer {
	private static final String EMPTY = null;
	private static boolean DEBUG = true;

	/** Demonstrates using the Speech API to transcribe an audio file. */
	public static void main(String... args) throws Exception {
		// Instantiates a client
		try (SpeechClient speechClient = SpeechClient.create()) {

			// initiate the CSV file
			try (PrintWriter writer = new PrintWriter(new File("Google_Open_SR_ResultsV5.csv"))) {

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
				/*
				 * sb.append(','); sb.append("different words"); sb.append(',');
				 * sb.append("original words");
				 */
				sb.append(',');
				sb.append("Accuracy");
				sb.append(',');
				sb.append("WER");
				/*
				 * sb.append(','); sb.append("gender"); sb.append(','); sb.append("accent");
				 * sb.append(','); sb.append("native"); sb.append(','); sb.append("country");
				 */
				sb.append('\n');
				writer.write(sb.toString());

				// The path to the audio file to transcribe
				String FileName = "";
				@SuppressWarnings("unused")
				int sequence = 1;
				RecognitionConfig config = null;
				RecognitionAudio audio;
				String[] reference = new String[5];

				// the original scripts of references
				reference[0] = "Put this blue block on the table Grab the block you just put on the table Place it there Take the yellow block and place it next to it Take one more block Put it on the top of those two blocks Stop you are done";
				reference[1] = "Go there Grasp this purple block Now put it in front of the cup Pick up the block that you just put down Now move that block and put it on top of these boxes Wait Move it more this way to the left Stop";
				reference[2] = "Take that red block Grab it and place it above these brown blocks Turn the block that is beside the white block to the right Never mind Take that block and put it behind the blue block That is enough";
				reference[3] = "OK turn left No your left Move the orange block to the right of that gray block Wait Grasp the black block Move it forward Put it next to that block Go here put this pink block between these ones That's pretty good";
				reference[4] = "There take that green plate and put it to the left of that mug OK scoot it back toward me Now push that glass behind that bottle there Now you're going to take this knife and drop it in the glass";

				File pathdir = null;
				String path_part1 = "";

				// there are 110 recordings for each reference's script
				// this loop goes through scripts
				// script1 folder includes recordings for the reference in index 0; similarly
				// for script2, script3, script4, and script5
				// scripts folder inside Recog_Resources folder
				for (int e = 0; e < reference.length; e++) {
					if (e == 0) {
						pathdir = new File("./resources/Recog_Resources/script1/");
						path_part1 = "./resources/Recog_Resources/script1/";
						// pathdir = new File("./resources/openrecog/script1/");
						// path_part1 = "./resources/openrecog/script1/";
					} else if (e == 1) {
						pathdir = new File("./resources/Recog_Resources/script2/");
						path_part1 = "./resources/Recog_Resources/script2/";
						// pathdir = new File("./resources/openrecog/script2/");
						// path_part1 = "./resources/openrecog/script2/";
					} else if (e == 2) {
						pathdir = new File("./resources/Recog_Resources/script3/");
						path_part1 = "./resources/Recog_Resources/script3/";
						// pathdir = new File("./resources/openrecog/script3/");
						// path_part1 = "./resources/openrecog/script3/";
					} else if (e == 3) {
						pathdir = new File("./resources/Recog_Resources/script4/");
						path_part1 = "./resources/Recog_Resources/script4/";
						// pathdir = new File("./resources/openrecog/script4/");
						// path_part1 = "./resources/openrecog/script4/";
					} else if (e == 4) {
						pathdir = new File("./resources/Recog_Resources/script5/");
						path_part1 = "./resources/Recog_Resources/script5/";
						// pathdir = new File("./resources/openrecog/script5/");
						// path_part1 = "./resources/openrecog/script5/";
					}
					File[] files = pathdir.listFiles();
					for (int i = 0; i < files.length; i++) {
						if (files[i].isFile() && !files[i].getName().equals(".DS_Store")) { // this line weeds out other
																							// directories/folders
							// the audio files are named in this pattern: id-gender_accentAnativeBcountry
							FileName = files[i].getName();
							System.out.println(files.length);
							int dot = FileName.indexOf(".");
							String ext = FileName.substring(dot, FileName.length());
							// id
							int dash = FileName.indexOf("-");
							String id = FileName.substring(0, dash);
							// gender
							/*
							 * int underScore = FileName.indexOf("_"); String gender =
							 * FileName.substring(dash+1, underScore); //accent int a1 =
							 * FileName.indexOf("A"); String accent = FileName.substring(underScore+1,a1);
							 * //native int b = FileName.indexOf("B"); String native1 =
							 * FileName.substring(a1,b); //country String country =
							 * FileName.substring(b+1,dot);
							 */

							System.out.println(sequence + "/ " + FileName);
							String audioPath = path_part1 + FileName;

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
							} else if (ext.equals(".wav")) {
								config = RecognitionConfig.newBuilder().setAudioChannelCount(2)
										.setEncoding(AudioEncoding.LINEAR16).setLanguageCode("en-US").build();
							} else {
								config = RecognitionConfig.newBuilder().setAudioChannelCount(2)
										.setEncoding(AudioEncoding.LINEAR16).setLanguageCode("en-US").build();

							}

							audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

							// Performs speech recognition on the audio file
							RecognizeResponse response = speechClient.recognize(config, audio);
							List<SpeechRecognitionResult> results = response.getResultsList();
							String SRRESULT = "";
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
							String[] ref = reference[e].split(" ");
							Alignment a = werEval.align(ref, hyp);

							System.out.println(a);

							// difference

							/*
							 * List<String> falsewords = new ArrayList<>(); List<String> correctwords = new
							 * ArrayList<>();
							 * 
							 * String[] hyp1 = SRRESULT.toLowerCase().split(" "); String[] ref1 = reference
							 * [e].toLowerCase().split(" ");
							 * 
							 * for (int i9 = 0; i9 < hyp1.length; i9++) { if (!hyp1[i9].equals(ref1[i9])) {
							 * falsewords.add(hyp[i9]); correctwords.add(ref1[i9]); } }
							 * System.out.println("difference = " + falsewords.toString());
							 * System.out.println("original = " + correctwords.toString());
							 */

							// adding results to CSV files
							sb2.append(sequence++);
							sb2.append(',');
							sb2.append(id);
							sb2.append(',');
							sb2.append(reference[e]);
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
							/*
							 * sb2.append(','); sb2.append(falsewords.toString()); sb2.append(',');
							 * sb2.append(correctwords.toString());
							 */
							sb2.append(',');
							sb2.append((int) ((a.getNumCorrect() / (float) a.getReferenceLength()) * 100) + "%");
							sb2.append(',');
							sb2.append(((a.numSubstitutions + a.numInsertions + a.numDeletions)
									/ (float) a.getReferenceLength()));
							/*
							 * sb2.append(','); sb2.append(gender); sb2.append(','); sb2.append(accent);
							 * sb2.append(','); sb2.append(native1); sb2.append(','); sb2.append(country);
							 * sb2.append(',');
							 */
							sb2.append('\n');

							writer.write(sb2.toString());
							sb2.delete(0, sb2.length());

						} // end if

					} // end for loop
					System.out.println("Writing to CSV is done!");
				}
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}

	}

}
