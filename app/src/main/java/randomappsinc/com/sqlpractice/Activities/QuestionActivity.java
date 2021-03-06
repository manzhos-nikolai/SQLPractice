package randomappsinc.com.sqlpractice.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.fonts.IoniconsIcons;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnPageChange;
import randomappsinc.com.sqlpractice.Adapters.QuestionsPagerAdapter;
import randomappsinc.com.sqlpractice.Database.QuestionServer;
import randomappsinc.com.sqlpractice.Misc.Constants;
import randomappsinc.com.sqlpractice.Misc.Utils;
import randomappsinc.com.sqlpractice.R;

/**
 * Created by alexanderchiou on 10/31/15.
 */
// Loads questions for users to answer
public class QuestionActivity extends StandardActivity {
    @Bind(R.id.question_pager) ViewPager questionPager;
    @BindString(R.string.question_number) String questionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_container);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        questionPager.setAdapter(new QuestionsPagerAdapter(getFragmentManager()));

        int initialQuestion = getIntent().getIntExtra(Constants.QUESTION_NUMBER_KEY, 0);
        if (initialQuestion == 0) {
            setTitle(String.format(questionNumber, initialQuestion + 1));
        } else {
            questionPager.setCurrentItem(initialQuestion);
        }
    }

    @OnPageChange(R.id.question_pager)
    public void onQuestionChanged() {
        setTitle(String.format(questionNumber, questionPager.getCurrentItem() + 1));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            questionPager.setCurrentItem(questionPager.getCurrentItem() + 1, true);
        }
    }

    private void openWebpage(String idea) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(WebActivity.IDEA_KEY, idea);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question_menu, menu);
        Utils.loadMenuIcon(menu, R.id.random, IoniconsIcons.ion_shuffle);
        Utils.loadMenuIcon(menu, R.id.instructional_materials, IoniconsIcons.ion_information_circled);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.hideKeyboard(this);
        int currentPosition = questionPager.getCurrentItem();
        switch (item.getItemId()) {
            case R.id.random:
                int newPosition = Utils.getRandomQuestionIndex(currentPosition);
                questionPager.setCurrentItem(newPosition, true);
                return true;
            case R.id.instructional_materials:
                new MaterialDialog.Builder(this)
                        .title(R.string.materials_title)
                        .items(QuestionServer.getQuestionServer().getQuestion(currentPosition).getIdeas())
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                openWebpage(text.toString());
                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}