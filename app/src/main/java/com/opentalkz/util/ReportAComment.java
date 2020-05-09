package com.opentalkz.util;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.network.RequestResponse;
import com.yashoid.network.RequestResponseCallback;
import com.opentalkz.R;
import com.opentalkz.TTMOffice;
import com.opentalkz.model.comment.Comment;
import com.opentalkz.model.post.Post;
import com.opentalkz.network.ReportResponse;
import com.opentalkz.network.Requests;

import java.util.ArrayList;
import java.util.List;

public class ReportAComment implements Post {

    private static final int COMMENT_LENGTH_TO_SHOW = 30;

    public static void reportAComment(final Context context, Model post) {
        final String postId = post.get(Post.ID);

        final List<ModelFeatures> comments = post.get(COMMENTS);

        List<String> commentList = new ArrayList<>(comments.size());

        for (ModelFeatures comment: comments) {
            String commentString = comment.get(Comment.CONTENT);

            if (commentString.length() > COMMENT_LENGTH_TO_SHOW) {
                commentString = commentString.substring(0, COMMENT_LENGTH_TO_SHOW) + "...";
            }

            commentList.add(commentString);
        }

        new AlertDialog.Builder(context)
                .setAdapter(new ArrayAdapter<>(context, R.layout.reportacomment, commentList), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String commentId = comments.get(which).get(Comment.ID);

                        TTMOffice.runner(context).runUserAction(Requests.report(postId, commentId, -1, null), new RequestResponseCallback<ReportResponse>() {

                            @Override
                            public void onRequestResponse(RequestResponse<ReportResponse> response) {
                                int message = response.isSuccessful() ? R.string.report_reported : R.string.report_notreported;

                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        });
                    }

                })
                .show();
    }

}
