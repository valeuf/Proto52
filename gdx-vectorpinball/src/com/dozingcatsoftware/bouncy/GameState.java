
package com.dozingcatsoftware.bouncy;

public class GameState {

	boolean gameInProgress;

	int ballNumber;
	int extraBalls;
	int totalBalls = 3;

	long score;
	int scoreMultiplier;
	int bonusScore;
	int bonusMultiplier;
	boolean multiplierHeld;

	public void startNewGame () {
		score = 0;
		ballNumber = 1;
		scoreMultiplier = 1;
		bonusMultiplier = 1;
		multiplierHeld = false;
		gameInProgress = true;
	}

	public void doNextBall () {
		// if multipliers held, clear it, if not, reset multipliers to 1
		if (multiplierHeld) {
			multiplierHeld = false;
		} else {
			scoreMultiplier = 1;
			bonusMultiplier = 1;
		}

		if (extraBalls > 0) {
			--extraBalls;
		} else if (ballNumber < totalBalls) {
			++ballNumber;
		} else {
			gameInProgress = false;
		}
	}

	public void addScore (long points) {
		score += points * scoreMultiplier;
	}

	public void addExtraBall () {
		++extraBalls;
	}

	public void incrementScoreMultiplier () {
		++scoreMultiplier;
	}

	public void incrementBonusMultiplier () {
		++bonusMultiplier;
	}

	// autogenerated accessors

	public boolean isGameInProgress () {
		return gameInProgress;
	}

	public void setGameInProgress (boolean gameInProgress) {
		this.gameInProgress = gameInProgress;
	}

	public int getBallNumber () {
		return ballNumber;
	}

	public void setBallNumber (int ballNumber) {
		this.ballNumber = ballNumber;
	}

	public int getExtraBalls () {
		return extraBalls;
	}

	public void setExtraBalls (int extraBalls) {
		this.extraBalls = extraBalls;
	}

	public int getTotalBalls () {
		return totalBalls;
	}

	public void setTotalBalls (int totalBalls) {
		this.totalBalls = totalBalls;
	}

	public long getScore () {
		return score;
	}

	public void setScore (long score) {
		this.score = score;
	}

	public int getScoreMultiplier () {
		return scoreMultiplier;
	}

	public void setScoreMultiplier (int scoreMultiplier) {
		this.scoreMultiplier = scoreMultiplier;
	}

	public int getBonusScore () {
		return bonusScore;
	}

	public void setBonusScore (int bonusScore) {
		this.bonusScore = bonusScore;
	}

	public int getBonusMultiplier () {
		return bonusMultiplier;
	}

	public void setBonusMultiplier (int bonusMultiplier) {
		this.bonusMultiplier = bonusMultiplier;
	}

	public boolean isMultiplierHeld () {
		return multiplierHeld;
	}

	public void setMultiplierHeld (boolean value) {
		multiplierHeld = value;
	}

}
