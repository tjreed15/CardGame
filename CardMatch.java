import acm.graphics.*;
import java.awt.event.*;

public class CardMatch extends CardDeck{
	
//One of these must be Even
	private static final int NROWS = 3;
	private static final int NCOLUMNS = 2;
	private static final int NCARDS = NROWS*NCOLUMNS;
	
	private static final int X_SEP = 4;
	private static final int Y_SEP = 4;
	private static final int INITIAL_X = 150;
	private static final int INITIAL_Y = 150;
	
	private static final int PAUSE_TIME = 1000;

	CardDeck deck = new CardDeck();
	
	public void run(){
		deck = new CardDeck(NCARDS);
		addMouseListeners();
		newGame = true;
		while (newGame){
			deck.cards = deck.shuffled(deck.cards, NCARDS);
			initializeVariables();
			setUpBoard();
			while (gameOn){
				if(guessChecked){
					pause(PAUSE_TIME);
					if (match) removeCards();
					else flipCards();
					time += (PAUSE_TIME/1000);
					addClock();
				}
				if (nCardsLeft == 0) break;
				pause(1000);
				time ++;
				addClock();
			}
			youWin();
			removeAll();
		}
		
	}
		
	private void addClock(){
		remove(timer);
		timer = new GLabel("Time: " + time);
		add(timer, timerX, timerY);
	}
	
	private void addCardLabel(){
		remove(cLabel);
		cLabel = new GLabel("Cards Left:" + nCardsLeft);
		add(cLabel, clX, clY);
	}
	
	private void initializeVariables(){
		time = 0;
		timer = new GLabel("Time: " + time);
		timerX = 100;
		timerY = 100;
		add(timer, timerX, timerY);
		
		nCardsLeft = NCARDS;
		cLabel = new GLabel("Cards Left: " + nCardsLeft);
		clX = 100;
		clY = 130;
		add(cLabel, clX, clY);
		
		guess1 = true;
		gameOn = true;
	}
	
	private void setUpBoard(){	
		double x = INITIAL_X;
		double y = INITIAL_Y;
		for(int j=0;j<NROWS;j++){
			for(int i = 0; i<NCOLUMNS; i++){
				add(deck.getCard(j*NCOLUMNS + i).getCardBack(), x, y);
				deck.getCard(j*NCOLUMNS + i).setDirection(false);
				x += deck.getCard(0).getCardFront().getWidth() + X_SEP;
			}
			x = INITIAL_X;
			y += deck.getCard(0).getCardFront().getHeight() + Y_SEP;
		}
	}


	
	public void mouseClicked(MouseEvent e){
		GCompound it = (GCompound) getElementAt(e.getX(), e.getY());
		Card clicked = (it == null)? null:deck.getCard(it);
		if (e.getButton() == 1 && clicked != null && !guessChecked){
			if (guess1) firstGuess(clicked);
			else if (clicked != guessA) secondGuess(clicked);
		}
	}
	
	
	
	private void firstGuess(Card card){
		guessA = card;
		flip(card);
		guess1 = false;
	}
	
	private void secondGuess(Card card){
		guessB = card;
		flip(card);
		checkForMatch();
		guess1 = true;
	}
	
	private void checkForMatch(){
	//Check color match
		match = deck.isColorDroppable(guessA, guessB);
	// Checks number match
		match = match && (guessA.getValue() == guessB.getValue());
		guessChecked = true;
	}
	
	private void removeCards(){
		remove(guessA.getCardFront());
		remove(guessB.getCardFront());
		match = false;
		guessChecked = false;
		nCardsLeft -= 2;
		addCardLabel();
	}
	
	private void flipCards(){
		flip(guessA);
		flip(guessB);
		match = false;
		guessChecked = false;
	}
	
	
	
	
	
	
	
// Flips given card from back/front to front/back
	private void flip(Card card){
		double x, y;
		int down = deck.getLevelDown(card) -1;
		flashCard(card);
		if (card.isUp()){
			x = card.getCardFront().getX();
			y = card.getCardFront().getY();
			remove(card.getCardFront());
			card.setDirection(false);
			add(card.getCardBack(), x, y);
			for (int i = 0; i < down; i++) card.getCardBack().sendBackward();
		}
		else{
			x = card.getCardBack().getX();
			y = card.getCardBack().getY();
			remove(card.getCardBack());
			card.setDirection(true);
			add(card.getCardFront(), x, y);
			for (int i = 0; i < down; i++) card.getCardFront().sendBackward();
		}
				
	}
	
	// Brings card stack to front, then card to front
	private void flashCard(Card card){
		for (int i = -1*deck.getLevelUp(card); i<deck.getLevelDown(card); i++){
			if (deck.getStack(card, i) != null && deck.getStack(card, i).getOrder() <deck.getNCards() && deck.getStack(card, i).isUp()){
				getStack(card, i).getCardFront().sendToFront();
			}	
		}
		card.getCardFront().sendToFront();
		
	}
	
	private void youWin(){
		removeAll();
		GLabel banner = new GLabel("YOU WIN!");
		banner.setFont("SANS_SERIF-bold-80");
		double fx = (getWidth() - banner.getWidth())/2;
		double y = (getHeight() - banner.getHeight())/2;
		double speed = 1;
		for(double x = 0; x<fx; x+=speed){
			add(banner, x, y);
			pause(.05);
			remove(banner);
		}
		add(banner);
		pause(750);
		for(double x = fx; x<getWidth(); x+=speed){
			add(banner, x, y);
			pause(5);
			remove(banner);
		}
		add(banner, (getWidth() - banner.getWidth())/2, (getHeight() - banner.getHeight())/2);
	}
	
	
	private boolean guess1, gameOn, guessChecked, newGame;
	private int nCardsLeft, time, clX, clY, timerX, timerY;
	private GLabel timer, cLabel;
	private Card guessA, guessB;
	private boolean match;

}
