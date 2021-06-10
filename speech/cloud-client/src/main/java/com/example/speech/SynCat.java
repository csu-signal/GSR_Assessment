package com.example.speech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class SynCat {

	public List<String> sg_nouns = Arrays.asList("bottle", "block", "box", "mug", "cup", "knife", "plate", "one",
			"glass", "blog");

	public List<String> pl_nouns = Arrays.asList("bottles", "blocks", "boxes", "mugs", "cups", "knives", "plates",
			"ones", "glasses", "blogs");

	public List<String> sg_determiners = Arrays.asList("this", "that", "the");

	public List<String> pl_determiners = Arrays.asList("these", "those", "the");

	public List<String> adjectives = Arrays.asList("yellow", "red", "blue", "purple", "green", "orange", "white",
			"gray", "black", "pink", "brown");

	public List<String> shift = Arrays.asList("never mind", "wait");

	public List<String> trans_no_goal = Arrays.asList("pick up", "lift", "grab", "grasp", "take", "let go of",
			"ungrasp", "drop", "release", "find", "go to");

	public List<String> trans_goal = Arrays.asList("move", "put", "push", "pull", "slide", "place", "shift", "scoot",
			"servo", "bring");

	public List<String> prepositions = Arrays.asList("on the left of", "on the right of", "to the left of",
			"to the right of", "left of", "right of", "above", "below", "behind", "in", "on", "beside", "before",
			"around", "on top of", "on the top of", "in front of", "in back of", "on the front of", "on the back of",
			"to the back of", "to back of", "next to");

	public List<String> local_imperative = Arrays.asList("go there", "go here", "go forward", "go back", "turn left",
			"turn right", "explore", "patrol", "stop");

	public List<String> LocationTerms = Arrays.asList("on the left of", "on the right of", "to the left of",
			"to the right of", "left of", "right of", "above", "below", "behind", "in", "on", "beside", "before",
			"around", "on top of", "on the top of", "in front of", "in back of", "on the front of", "on the back of",
			"to the back of", "to back of", "next to", "here", "there");

	// making lists static so multiple searches are quicker
	public List<String> nps = Arrays.asList();

	public List<String> pps = Arrays.asList();

	public List<String> vps = Arrays.asList();

	public List<String> partial_vps = Arrays.asList();

	public List<String> sg_partial_nps = Arrays.asList();

	public List<String> pl_partial_nps = Arrays.asList();

	public List<String> NN = new ArrayList<String>();

	public List<String> NNS = new ArrayList<String>();

	public List<String> UserIntentObject;

	public List<String> UserIntentLocation;

	public List<String> UserIntentObjectJJ;

	public List<String> UserIntentObjectVV;

	public List<String> UserIntentObjectDD;

	MaxentTagger tagger = new MaxentTagger("taggers/english-bidirectional-distsim.tagger");

	public SynCat() {
		// generate singular NPs missing determiners (fragments)
		this.sg_partial_nps = this.SgPartialNPs();
		// generate plural NPs missing determiners (fragments)
		this.pl_partial_nps = this.PlPartialNPs();
		this.nps = this.GetNPs();
		this.pps = this.GetPPs();
		this.partial_vps = this.VPsNeedGoal();
	}

	// Taking a hashset of valid NPs,
	// this method generates all valid PPs over the
	// domain vocabulary
	public final ArrayList<String> GetPPs() {
		ArrayList<String> NPs = this.GetNPs();
		// This only generates PPs of depth one as
		// Diana doesn't handle phrases like
		// 'the block to the left of the yellow block'
		ArrayList<String> PPs = new ArrayList<String>();
		// attach prepositions to the front of NPs
		// where a determiner is present
		// (since *'on red block' and the like are marked)
		for (String p : this.prepositions) {
			for (String np : NPs) {
				String[] words = np.split(" ");
				String first = words[0];
				if ((this.sg_determiners.contains(first) || this.pl_determiners.contains(first))) {
					StringBuilder builder = new StringBuilder();
					builder.append(p).append(" ").append(np);
					PPs.add(builder.toString().trim());
				}

			}

		}

		return PPs;
	}

	// Taking hashses of valid singular and plural NP fragments,
	// this method generates all valid NPs over the
	// domain vocabulary
	public final ArrayList<String> GetNPs() {
		ArrayList<String> sg_partials = this.SgPartialNPs();
		ArrayList<String> pl_partials = this.PlPartialNPs();
		ArrayList<String> sg_full = new ArrayList<String>();
		ArrayList<String> pl_full = new ArrayList<String>();
		ArrayList<String> NPs = new ArrayList<String>();
		// coordinate singular determiners with singular fragments
		for (String sg_det : this.sg_determiners) {
			for (String nn : sg_partials) {
				StringBuilder builder = new StringBuilder();
				builder.append(sg_det).append(" ").append(nn);
				sg_full.add(builder.toString().trim());
			}

		}

		// coordinate plural determiners with plural fragments
		for (String pl_det : this.pl_determiners) {
			for (String nn : pl_partials) {
				StringBuilder builder = new StringBuilder();
				builder.append(pl_det).append(" ").append(nn);
				pl_full.add(builder.toString().trim());
			}

		}

		// consider NPs with and without determiners for ease of recognition
		NPs.addAll(sg_partials);
		NPs.addAll(pl_partials);
		NPs.addAll(sg_full);
		NPs.addAll(pl_full);
		return NPs;
	}

	// Taking a hashset of valid NPs,
	// this method generates all valid PPs over the
	// domain vocabulary
	public final ArrayList<String> VPsNeedGoal() {
		ArrayList<String> NPs = this.GetNPs();
		ArrayList<String> VP_trans_goal = new ArrayList<String>();
		// for each V-head which subcats for a theme
		// and a goal, attach only an NP theme, ommitting
		// the PP goal
		for (String vtg : this.trans_goal) {
			for (String np : NPs) {
				StringBuilder builder = new StringBuilder();
				builder.append(vtg).append(" ").append(np);
				VP_trans_goal.add(builder.toString().trim());
			}

		}

		return VP_trans_goal;
	}

	// this method generates singular
	// NPs missing determiners (fragments)
	public final ArrayList<String> SgPartialNPs() {
		ArrayList<String> sg_adj_no_det = new ArrayList<String>();
		ArrayList<String> NPs = new ArrayList<String>();
		// generate fragments with adjectives
		for (String adj : this.adjectives) {
			for (String nn : this.sg_nouns) {
				StringBuilder builder = new StringBuilder();
				builder.append(adj).append(" ").append(nn);
				sg_adj_no_det.add(builder.toString().trim());
			}

		}

		// We need to consider both the forms with and without adjectives
		NPs.addAll(this.sg_nouns);
		NPs.addAll(sg_adj_no_det);
		return NPs;
	}

	// this method generates plural
	// NPs missing determiners (fragments)
	public final ArrayList<String> PlPartialNPs() {
		ArrayList<String> pl_adj_no_det = new ArrayList<String>();
		ArrayList<String> NPs = new ArrayList<String>();
		// generate fragments with adjectives
		for (String adj : this.adjectives) {
			for (String nn : this.pl_nouns) {
				StringBuilder builder = new StringBuilder();
				builder.append(adj).append(" ").append(nn);
				pl_adj_no_det.add(builder.toString().trim());
			}

		}

		// We need to consider both the forms with and without adjectives
		NPs.addAll(pl_adj_no_det);
		NPs.addAll(this.pl_nouns);
		return NPs;
	}

	// this method returns the singular and plural nouns from the audio scripts

	public List<String> getUserIntentObj(String script) {
		List<String> UserIntentObject1 = new ArrayList<String>();

		String tag = "";
		String word;
		int US = 0;
		String tagged = tagger.tagString(script);
		String[] tokens = tagged.split("\\s+");
		for (int i = 0; i < tokens.length; i++) { //

			// You may want to check for a non-word character before blindly
			// performing a replacement
			// It may also be necessary to adjust the character class
			tokens[i] = tokens[i].replaceAll("[^\\w]", "");
			US = tokens[i].indexOf("_");

			// tag and words extraction
			tag = tokens[i].substring(US + 1, tokens[i].length());
			word = tokens[i].substring(0, US);
			// NOUN SINGULAR
			if (tag.equals("NN")) {
				if (!UserIntentObject1.contains(word)) {
					UserIntentObject1.add(word);
				}
			}
			// NOUN PLURAL
			if (tag.equals("NNS")) {
				if (!UserIntentObject1.contains(word)) {
					UserIntentObject1.add(word);
				}
			}

		}
		return UserIntentObject1;

	}

	public List<String> getUserIntentObddd(String script) {
		List<String> UserIntentObjectDD1 = new ArrayList<String>();

		String tag = "";
		String word;
		int US = 0;
		String tagged = tagger.tagString(script);
		String[] tokens = tagged.split("\\s+");
		for (int i = 0; i < tokens.length; i++) { //

			// You may want to check for a non-word character before blindly
			// performing a replacement
			// It may also be necessary to adjust the character class
			tokens[i] = tokens[i].replaceAll("[^\\w]", "");
			US = tokens[i].indexOf("_");

			// tag and words extraction
			tag = tokens[i].substring(US + 1, tokens[i].length());
			word = tokens[i].substring(0, US);

			// ADJECTIVE
			if (tag.equals("DT")) {
				if (!UserIntentObjectDD1.contains(word)) {
					UserIntentObjectDD1.add(word);
				}
			}
		}
		return UserIntentObjectDD1;

	}

	public List<String> getUserIntentObjjj(String script) {
		List<String> UserIntentObjectJJ1 = new ArrayList<String>();

		String tag = "";
		String word;
		int US = 0;
		String tagged = tagger.tagString(script);
		String[] tokens = tagged.split("\\s+");
		for (int i = 0; i < tokens.length; i++) { //

			// You may want to check for a non-word character before blindly
			// performing a replacement
			// It may also be necessary to adjust the character class
			tokens[i] = tokens[i].replaceAll("[^\\w]", "");
			US = tokens[i].indexOf("_");

			// tag and words extraction
			tag = tokens[i].substring(US + 1, tokens[i].length());
			word = tokens[i].substring(0, US);

			// ADJECTIVE
			if (tag.equals("JJ")) {
				if (!UserIntentObjectJJ1.contains(word)) {
					UserIntentObjectJJ1.add(word);
				}
			}
		}
		return UserIntentObjectJJ1;

	}

	public List<String> getUserIntentObvvv(String script) {
		List<String> UserIntentObjectVV1 = new ArrayList<String>();

		String tag = "";
		String word;
		int US = 0;
		String tagged = tagger.tagString(script);
		String[] tokens = tagged.split("\\s+");
		for (int i = 0; i < tokens.length; i++) { //

			// You may want to check for a non-word character before blindly
			// performing a replacement
			// It may also be necessary to adjust the character class
			tokens[i] = tokens[i].replaceAll("[^\\w]", "");
			US = tokens[i].indexOf("_");

			// tag and words extraction
			tag = tokens[i].substring(US + 1, tokens[i].length());
			word = tokens[i].substring(0, US);

			// ADJECTIVE
			if (tag.equals("VB")) {
				if (!UserIntentObjectVV1.contains(word)) {
					UserIntentObjectVV1.add(word);
				}
			}
		}
		return UserIntentObjectVV1;

	}

	// this method returns the location terms from the audio scripts

	public List<String> getUserIntentLocation(String script) {
		List<String> UserIntentLocation1 = new ArrayList<String>();

		String[] tokens = script.split("\\s+");
		for (int i = 0; i < tokens.length; i++) { //

			// You may want to check for a non-word character before blindly
			// performing a replacement
			// It may also be necessary to adjust the character class
			if (LocationTerms.contains(tokens[i])) {
				UserIntentLocation1.add(tokens[i]);
			}

		}
		return UserIntentLocation1;

	}

	// this method returns the syntactic category,
	// fragment, or part of speech, most likely to be seen
	// given the content (or lack thereof) in the blackboard variables
	// UserIntentObject and UserIntentLocation
	public final List<String> Predict(String Script) {
		// child of GoogleSR object that stores "user:intent:object" nouns
		this.UserIntentObject = this.getUserIntentObj(Script);
		// objectives
		this.UserIntentObjectJJ = this.getUserIntentObjjj(Script);
		// verbs
		this.UserIntentObjectVV = this.getUserIntentObvvv(Script);
		// DT
		this.UserIntentObjectDD = this.getUserIntentObddd(Script);

		// child of GoogleSR object that stores "user:intent:location"
		this.UserIntentLocation = this.getUserIntentLocation(Script);
		// get the retrieved value of "user:intent:object" by:
		// if userIntentObject is not null, then there's an object on the blackboard
		// get the retrieved value of "user:intent:location" by:
		// if userIntentLocation is not (0,0,0), then there's an location on the
		// blackboard
		// initializing otherwise VS complains in the conditionals
		ArrayList<String> Predictions = new ArrayList<String>();
		// if userIntentObject has not been initialized or modified
		// then return the most likely syntactic category, namely NP

		if ((this.UserIntentObject.isEmpty())) {
			Predictions.addAll(this.nps);
			Predictions.addAll(this.trans_no_goal);
			Predictions.addAll(this.trans_goal);
			// Predictions.add("here first branch");
			// System.out.println("here first branch");

			// Predictions.AddRange(this.partial_vps);
			return Predictions;
		} else {

			// for shift verbs we want to bias towards a new object
			// or location, e.g., NP or PP

			for (int i = 0; i < this.UserIntentObject.size(); i++) {
				for (int j = 0; j < this.UserIntentObjectJJ.size(); j++) {
					String v = UserIntentObject.get(i);
					String h = UserIntentObjectJJ.get(j);

					// System.out.println("for s.tirm in while"+s.trim());
					// System.out.println(key1[i].toString());

					if (sg_nouns.contains(v) || pl_nouns.contains(v)) {

						// check the similar words to the sripts in nps and pps
						for (int e = 0; e < nps.size(); e++) {
							if ((nps.get(e).contains(v)) && (nps.get(e).contains(h))) {
								Predictions.add(nps.get(e));
							}
							// Predictions.addAll(this.nps);
							// Predictions.addAll(this.pps);
							// return Predictions;
						}
						for (int e = 0; e < pps.size(); e++) {
							if ((pps.get(e).contains(v)) && (pps.get(e).contains(h))) {
								Predictions.add(pps.get(e));
							}
							// Predictions.addAll(this.nps);
							// Predictions.addAll(this.pps);
							// return Predictions;
						}
					}
					// break;

				}
			}

			// singular determiners are most likely to be followed
			// by singular NP fragments
			/*
			 * for (int i = 0; i < this.UserIntentObject.size(); i++) { for (int j=0; j<
			 * this.UserIntentObjectJJ.size(); j++) { for (int d=0; d<
			 * this.UserIntentObjectDD.size(); d++) { String v = UserIntentObject.get(i);
			 * String h= UserIntentObjectJJ.get(j); String r= UserIntentObjectDD.get(d);
			 * 
			 * if (this.sg_determiners.contains(r)) {
			 * 
			 * //Predictions.add(v +" + "+ h+" + "+ r);
			 * //Predictions.addAll(sg_partial_nps); //return this.sg_partial_nps; for (int
			 * e=0; e < sg_partial_nps.size(); e++) { // this compinations do not include
			 * determiners if ((sg_partial_nps.get(e).contains(v))&&
			 * (sg_partial_nps.get(e).contains(h)) ) {
			 * Predictions.add(sg_partial_nps.get(e)); } }
			 * 
			 * } } //break; } }
			 */

			// transactional verbs are most likely to be followed
			//
			for (int i = 0; i < this.UserIntentObject.size(); i++) {
				for (int j = 0; j < this.UserIntentObjectJJ.size(); j++) {
					for (int f = 0; f < this.UserIntentObjectVV.size(); f++) {

						String v = UserIntentObject.get(i);// noun
						String h = UserIntentObjectJJ.get(j);// objective
						String y = UserIntentObjectVV.get(f);// verb

						// System.out.println("for s.tirm in while"+s.trim());
						// System.out.println(key1[i].toString());
						if (this.trans_goal.contains(y)) {
							// Predictions.add(v +" + "+ h+" + "+ y);

							// Predictions.addAll(sg_partial_nps);
							// return this.sg_partial_nps;
							for (int e = 0; e < this.partial_vps.size(); e++) {
								// this compinations do not include determiners
								if ((this.partial_vps.get(e).contains(y)) && (this.partial_vps.get(e).contains(v))
										&& (this.partial_vps.get(e).contains(h))) {
									Predictions.add(this.partial_vps.get(e));
								}
							}

						}
					}
				}
				// break;

			}

			// plural determiners are most likely to be followed
			// by plural NP fragments
			/*
			 * for (int i = 0; i < this.UserIntentObject.size(); i++) { for (int j=0; j<
			 * this.UserIntentObjectJJ.size(); j++) { String v = UserIntentObject.get(i);
			 * String h= UserIntentObjectJJ.get(j);
			 * 
			 * // System.out.println("for s.tirm in while"+s.trim()); //
			 * System.out.println(key1[i].toString()); if (this.pl_determiners.contains(v))
			 * { //Predictions.addAll(pl_partial_nps); //return this.pl_partial_nps; for
			 * (int e=0; e < pl_partial_nps.size(); e++) { if
			 * ((pl_partial_nps.get(e).contains(v)) && (pl_partial_nps.get(e).contains(h)))
			 * { Predictions.add(pl_partial_nps.get(e)); } } } } break;
			 * 
			 * }
			 */

			// all non-switch V-heads immediately subcat for an NP,
			// likewise for all P-heads
			/*
			 * for (int i = 0; i < this.UserIntentObject.size(); i++) { for (int j=0; j<
			 * this.UserIntentObjectJJ.size(); j++) { String v = UserIntentObject.get(i);
			 * String h= UserIntentObjectJJ.get(j);
			 * 
			 * //Predictions.add(v); // System.out.println("for s.tirm in while"+s.trim());
			 * // System.out.println(key1[i].toString()); if ((this.trans_goal.contains(v)
			 * || (this.trans_no_goal.contains(v) || this.prepositions.contains(v)))) {
			 * //Predictions.addAll(nps); //return this.nps; for (int e=0; e < nps.size();
			 * e++) { if ((nps.get(e).contains(v)) && (nps.get(e).contains(h))) {
			 * Predictions.add(nps.get(e)); } } } } break;
			 * 
			 * }
			 */

			// if no location has been stored we need to bias for a PP,
			// likewise if we've just seen a full or partial NP
			// (since if there's no goal to currently
			// specify the speaker won't say anything)

			/*
			 * for (int i = 0; i < this.UserIntentObject.size(); i++) { for (int j=0; j<
			 * this.UserIntentObjectJJ.size(); j++) { String v = UserIntentObject.get(i);
			 * String h= UserIntentObjectJJ.get(j); Predictions.add(v +" + "+ h); if
			 * ((!(UserIntentLocation.isEmpty()) || (this.partial_vps.contains(v) ||
			 * (this.nps.contains(v) || (this.sg_partial_nps.contains(v) ||
			 * this.pl_partial_nps.contains(v)))))) { //Predictions.addAll(pps); for (int
			 * e=0; e < pps.size(); e++) { if ((pps.get(e).contains(v)) &&
			 * (pps.get(e).contains(h))) { Predictions.add(pps.get(e)); } } //return
			 * this.pps; } } break;
			 * 
			 * }
			 */

//System.out.println("here second branch");
			// For the default case,
			// NPs are the most likely syntactic category
			// Predictions.add("here second branch");
			return Predictions;

		}
	}

}
