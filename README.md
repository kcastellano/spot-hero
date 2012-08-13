#Spot Hero

Music has always been a way to express our feelings and thoughts. Even though some people don’t have the abilities to play a real instrument, they always could try to develop some skills in games that emulate the music playing. Such games as Guitar Hero or Rock Band present the opportunity to simplify the act of learning how to do the basic hand movements or understanding the rhythm that leads to almost mastering an instrument.

The purpose of this project is to present a much easier way to learn and play several notes by using Java SunSPOTs. By understanding the limitations and the qualities of the SPOTs the project tries to emulate in the most real way the music playing process by making the player strum different notes in different tones by moving several of the SPOTs.

Spot Hero is the final project for the NCSU/UCAB Summer Practicum in Computer Science for 2010, developed by [Khaterine Castellano](https://github.com/kcastellano) and [Jonathan Trujillo](https://github.com/jonotrujillo). Project presentation [slides](https://speakerdeck.com/u/jonotrujillo/p/spot-hero) are available.

##Project Requirements

For the project development, the following scope was chosen:

*	Emulate the strumming process of a Guitar with the free range SunSPOT accelerometer.
*	Emulate the position in the fret with a free range Spot to determine the note and tone.
*	Record the readings from both of the SPOTs with a base station and play the notes.

##Solution Design

To achieve the main purpose the project used four SunSPOTs. Three free range SPOTs, one for the strum, one for the pocket and one for the neck, and to record the strum and position values a base station was used.
For the strumming process there was a free range Spot moving upwards and downwards (accelerometer) called Pick that emulated the hand movements in the Guitar. The only axis that was taken to notice was the Z, and the values had to be below 0.5.

To determine the tone and note there were two free range Spots called Guitar Neck and a Pocket. To establish the position in the fret the radio and RSSI was used between both of the SPOTS so the more closer they were the tone was higher and the farthest they were there was the lower.

To record the fret value and the strum there is a Host Application running with a base station. What it does is when the strumming is done it sends the tilt to the base and it records the RSSI between the Neck and the Pocket so it can determine in a range of values the tone and the note it needs to play.

##Testing

To test the application the SunSPOTS were positioned in different distance between the Neck and the Pocket to get different tones, also the Pick was moved in different speed rates to determine when the notes would be played. For each note a color was displayed in the neck SunSPOT so the player would determine which note it was playing.

##Results

The results obtained with this project were very successful. By using the radio strength between the pocket spot and the neck spot the application could calculate the distance and determine in which part of the fret was positioned to establish which note to play, with the values obtained the host application could transform to each of the notes in the chords.

##Issues

The issues encountered in the project were:

*	The library used for the note playing is JFugue, but it only allows to play 17 consecutive notes in an application.
*	Some of the spots have channel noise, so it doesn't permit to establish one channel between them.