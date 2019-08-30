package com.yashoid.talktome;

import android.content.Context;
import android.content.SharedPreferences;

import com.yashoid.mmv.ModelFeatures;
import com.yashoid.talktome.model.post.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TTMCompat {

    private static final String PREFERENCES = "com.yashoid.talktome";

    private static final String KEY_NOTES = "notes";

    private static final String ID = "note_id";
    private static final String NOTE = "note_note";
    private static final String TIME = "note_time";
    private static final String DISPLAYS = "note_displays";

    public static List<ModelFeatures> getLegacyNotes(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        String sNotes = prefs.getString(KEY_NOTES, "[]");

        List<ModelFeatures> notes = new ArrayList<>();

        try {
            JSONArray jNotes = new JSONArray(sNotes);

            for (int i=0; i<jNotes.length(); i++) {
                notes.add(getNoteFeatures(jNotes.getJSONObject(i)));
            }
        } catch (JSONException e) {

        } finally {
            prefs.edit().remove(KEY_NOTES).apply();
        }

        return notes;
    }

    private static ModelFeatures getNoteFeatures(JSONObject jNote) throws JSONException {
        ModelFeatures.Builder postBuilder = new ModelFeatures.Builder();

        postBuilder.add(Post.ID, String.valueOf(jNote.getLong(ID)));
        postBuilder.add(Post.CONTENT, jNote.getString(NOTE));

        try {
            Long createdTime = !jNote.isNull(TIME)?jNote.getLong(TIME):null;
            postBuilder.add(Post.CREATED_TIME, createdTime);
        } catch (Throwable t) { }

        postBuilder.add(Post.VIEWS, jNote.getInt(DISPLAYS));

        return postBuilder.build();
    }

}
