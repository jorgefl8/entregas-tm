package com.jorge.memory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MemoryAdapter.OnCardClickListener {

    private static final String PREFS_NAME = "game_prefs";
    private static final String KEY_BEST_SCORE = "best_score";
    private static final long HIDE_DELAY_MS = 700L;

    private TextView subtitleText;
    private TextView attemptsText;
    private TextView bestScoreText;
    private TextView statusText;
    private RecyclerView boardRecycler;
    private Button actionButton;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<CardItem> cards = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private MemoryAdapter memoryAdapter;

    private int firstSelectedIndex = RecyclerView.NO_POSITION;
    private int secondSelectedIndex = RecyclerView.NO_POSITION;
    private int attempts;
    private int bestScore;
    private boolean boardLocked;
    private boolean gameStarted;
    private boolean gameFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        bestScore = sharedPreferences.getInt(KEY_BEST_SCORE, 0);

        bindViews();
        configureRecyclerView();
        showInitialState();
    }

    private void bindViews() {
        subtitleText = findViewById(R.id.subtitleText);
        attemptsText = findViewById(R.id.attemptsText);
        bestScoreText = findViewById(R.id.bestScoreText);
        statusText = findViewById(R.id.statusText);
        boardRecycler = findViewById(R.id.boardRecycler);
        actionButton = findViewById(R.id.actionButton);

        actionButton.setOnClickListener(v -> {
            if (!gameStarted || gameFinished) {
                startGame();
            } else {
                resetGame();
            }
        });
    }

    private void configureRecyclerView() {
        memoryAdapter = new MemoryAdapter(cards, this);
        boardRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        boardRecycler.setAdapter(memoryAdapter);
    }

    private void showInitialState() {
        gameStarted = false;
        gameFinished = false;
        boardLocked = false;
        attempts = 0;
        cards.clear();
        firstSelectedIndex = RecyclerView.NO_POSITION;
        secondSelectedIndex = RecyclerView.NO_POSITION;

        subtitleText.setText(R.string.subtitle_start);
        attemptsText.setText(getString(R.string.attempts_value, attempts));
        bestScoreText.setText(getBestScoreLabel());
        statusText.setText(R.string.status_start);
        statusText.setTextColor(getColor(R.color.feedbackNeutral));
        actionButton.setText(R.string.start_game);
        actionButton.setVisibility(View.VISIBLE);
        boardRecycler.setAlpha(0.55f);
        memoryAdapter.updateBoard();
    }

    private void startGame() {
        handler.removeCallbacksAndMessages(null);
        cards.clear();
        cards.addAll(buildDeck());
        Collections.shuffle(cards);

        attempts = 0;
        boardLocked = false;
        gameStarted = true;
        gameFinished = false;
        firstSelectedIndex = RecyclerView.NO_POSITION;
        secondSelectedIndex = RecyclerView.NO_POSITION;

        subtitleText.setText(R.string.subtitle_playing);
        statusText.setText(R.string.status_playing);
        statusText.setTextColor(getColor(R.color.feedbackNeutral));
        actionButton.setText(R.string.restart_game);
        actionButton.setVisibility(View.VISIBLE);
        boardRecycler.setAlpha(1f);

        updateScoreLabels();
        memoryAdapter.updateBoard();
    }

    private void resetGame() {
        startGame();
    }

    @Override
    public void onCardClick(int position) {
        if (!gameStarted || gameFinished || boardLocked) {
            return;
        }

        CardItem tappedCard = cards.get(position);
        if (tappedCard.isMatched() || tappedCard.isRevealed()) {
            return;
        }

        tappedCard.setRevealed(true);
        memoryAdapter.updateCard(position);

        if (firstSelectedIndex == RecyclerView.NO_POSITION) {
            firstSelectedIndex = position;
            statusText.setText(R.string.status_second_pick);
            return;
        }

        if (position == firstSelectedIndex) {
            return;
        }

        secondSelectedIndex = position;
        attempts++;
        updateScoreLabels();
        evaluateSelection();
    }

    private void evaluateSelection() {
        CardItem firstCard = cards.get(firstSelectedIndex);
        CardItem secondCard = cards.get(secondSelectedIndex);

        if (firstCard.getPairId() == secondCard.getPairId()) {
            firstCard.setMatched(true);
            secondCard.setMatched(true);
            memoryAdapter.updateCard(firstSelectedIndex);
            memoryAdapter.updateCard(secondSelectedIndex);
            statusText.setText(R.string.status_match);
            clearSelection();

            if (allPairsFound()) {
                showFinalState();
            }
            return;
        }

        boardLocked = true;
        statusText.setText(R.string.status_try_again);
        handler.postDelayed(() -> {
            firstCard.setRevealed(false);
            secondCard.setRevealed(false);
            memoryAdapter.updateCard(firstSelectedIndex);
            memoryAdapter.updateCard(secondSelectedIndex);
            clearSelection();
            boardLocked = false;
            if (!gameFinished) {
                statusText.setText(R.string.status_playing);
            }
        }, HIDE_DELAY_MS);
    }

    private void showFinalState() {
        gameFinished = true;
        boardLocked = true;
        subtitleText.setText(R.string.subtitle_finished);
        statusText.setText(getString(R.string.status_finished, attempts));
        statusText.setTextColor(getColor(R.color.feedbackCorrect));
        boardRecycler.setAlpha(0.92f);
        actionButton.setText(R.string.play_again);

        if (bestScore == 0 || attempts < bestScore) {
            bestScore = attempts;
            sharedPreferences.edit().putInt(KEY_BEST_SCORE, bestScore).apply();
        }

        updateScoreLabels();
    }

    private void clearSelection() {
        firstSelectedIndex = RecyclerView.NO_POSITION;
        secondSelectedIndex = RecyclerView.NO_POSITION;
    }

    private void updateScoreLabels() {
        attemptsText.setText(getString(R.string.attempts_value, attempts));
        bestScoreText.setText(getBestScoreLabel());
    }

    private String getBestScoreLabel() {
        if (bestScore == 0) {
            return getString(R.string.best_score_empty);
        }
        return getString(R.string.best_score_value, bestScore);
    }

    private boolean allPairsFound() {
        for (CardItem card : cards) {
            if (!card.isMatched()) {
                return false;
            }
        }
        return true;
    }

    private List<CardItem> buildDeck() {
        int[] images = {
                R.drawable.casa,
                R.drawable.coche,
                R.drawable.sol,
                R.drawable.estrella,
                R.drawable.corazon,
                R.drawable.telefono,
                R.drawable.camara,
                R.drawable.bicicleta
        };

        List<CardItem> deck = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            deck.add(new CardItem(images[i], i));
            deck.add(new CardItem(images[i], i));
        }
        return deck;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
