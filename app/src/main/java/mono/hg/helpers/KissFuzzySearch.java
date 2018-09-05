package mono.hg.helpers;

import android.util.Pair;
import java.util.ArrayList;

/**
 * Fuzzy search class, code taken from works of saveman71 at KISS Launcher.
 * https://github.com/Neamar/KISS/commit/41aaec1e27da79fea7c929146cbababe3acac64e
 *
 * Parts have been slightly altered to fit our codes here.
 */

public class KissFuzzySearch {

    public static int doFuzzy(String sourceName, String queryMatch) {
        ArrayList<Pair<Integer, Integer>> matchPositions = new ArrayList<>();

        int relevance = 0, queryPos = 0, appPos = 0, beginMatch = 0,
                matchedWordStarts = 0, totalWordStarts = 0;

        // Normalise query and source (app name).
        String source = sourceName.toLowerCase();
        String matchTo = queryMatch.toLowerCase().trim();

        boolean match = false;

        for (char cApp : source.toCharArray()) {
            if (queryPos < matchTo.length() && matchTo.charAt(queryPos) == cApp) {
                // If we aren't already matching something, let's save the beginning of the match
                if (!match) {
                    beginMatch = appPos;
                    match = true;
                }

                // If we are at the beginning of a word, add it to matchedWordStarts
                if (appPos == 0 || Character.isWhitespace(source.charAt(appPos - 1))) {
                    matchedWordStarts += 1;
                }

                // Increment the position in the query
                queryPos++;
            } else if (match) {
                matchPositions.add(Pair.create(beginMatch, appPos));
                match = false;
            }

            // If we are at the beginning of a word, add it to totalWordsStarts
            if (appPos == 0 || Character.isWhitespace(source.charAt(appPos - 1))) {
                totalWordStarts += 1;
            }
            appPos++;
        }

        if (match) {
            matchPositions.add(Pair.create(beginMatch, appPos));
        }

        if (queryPos == matchTo.length()) {
            // Add percentage of matched letters at a weight of 100
            relevance += (int) (((double) queryPos / source.length()) * 100);

            // Add percentage of matched upper case letters (start of word), but at a weight of 60
            relevance += (int) (((double) matchedWordStarts / totalWordStarts) * 60);

            // The more fragmented the matches are, the less the result is important
            relevance = (int) (relevance * (0.2 + 0.8 * (1.0 / matchPositions.size())));
        }
        return relevance;
    }
}
