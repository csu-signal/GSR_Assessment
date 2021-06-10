# Getting Started with Google Speech Recognition Assessment

We conducted a study to assess the quality of google speech recognizer by collecting audio recordings of 5 domain specific scrips and that results with 110 samples. openRecognizer and restrictedRecognizer evaluations code used to assess open vocabulary google speech recognizer vs restricted google speech recognizer.

Before beginning, complete the following steps as listed on the [Speech-to-Text Docs pages for Getting Started](https://cloud.google.com/speech-to-text/docs/quickstart-client-libraries#before-you-begin)

## OpenRecognizer Evaluation
- Transcribe local audio files thst are stored in resources folder
- Compare the transcriptions with the ground truth scripts (references)
- Find some statistics: number of Deletions, number of Insertions, number of Substitutions 
- Compute accuracy and Word Error Rate (WER)

## RestrictedRecognizer Evaluation
- Send syntactically adaptive domain vocabulary to the speech context of GSR 
- Transcribe local audio files thst are stored in resources folder
- Compare the transcriptions with the ground truth scripts (references)
- Find some statistics: number of Deletions, number of Insertions, number of Substitutions 
- Compute accuracy and Word Error Rate (WER)

