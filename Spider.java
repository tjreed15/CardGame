import acm.graphics.*;
import acm.util.RandomGenerator;
import java.awt.event.*;

public class Spider extends CardDeck{

	private static final int CARD_STACK_OFFSET = 35;
	private static final int NCARDS = 52;
	private static final int NJOKERS = 0;
	private static final int NSPACES = 25;
	
	private static final int INITIAL_X = 150;
	private static final int INITIAL_Y = 20;
	private static final int X_SEP = 4;
	
	
	
	private CardDeck deck;
	
	public void run(){
		deck = new CardDeck(NCARDS, 0, NSPACES);
		addMouseListeners();
		newGame = true;
		while (newGame){
			deck.cards = deck.shuffled(deck.cards, NCARDS);
			initializeVariables();
			setUpBoard();
			while (gameOn){
				if (!gameOn) break;
			}
			youWin();
			removeAll();
		}
	}

	private void initializeVariables(){
		nStacks = 0;
		index = 0;
		gameOn = true;
	}
	
	private void setUpBoard(){
		
	}
	
	private void addColumn(int j){
		double x = INITIAL_X + j*(deck.getCard(0).getCardFront().getWidth() + X_SEP);
		double y = INITIAL_Y;
		add(deck.getCard(deck.getNCards()+j).getCardFront(), x, y);
		for(int i=0;i<(j+1);i++){
			y = INITIAL_Y+ CARD_STACK_OFFSET*(i);
			GCompound down = (GCompound) getElementAt(x+(deck.getCard(0).getWidth()/2), y + 5);
			if(index<deck.getNCards()){
				if (i == j) add(deck.getCard(index).getCardFront(), x, y);
				else {
					deck.getCard(index).setDirection(false);
					add(deck.getCard(index).getCardBack(), x, y);
				}
				deck.getCard(index).setSpecial("");
				Card covered = deck.getCard(down);
				if(covered != null){
					covered.setCovered(deck.getCard(index));
					deck.getCard(index).setCovers(covered);
				}
			}
			index++;
		}
	}
	
	public void mouseClicked(MouseEvent e){
		GCompound it = (GCompound) getElementAt(e.getX(), e.getY());
		Card clicked = deck.getCard(it);
		if (e.getButton() == 1){
			if (clicked != null && clicked.getOrder() < deck.getNCards()) flip(clicked);
		}
	}
	
	public void mousePressed(MouseEvent e){
		last = new GPoint (e.getPoint());
		GCompound it = (GCompound) getElementAt(e.getX(), e.getY());
		Card pressed = deck.getCard(it);
		if(pressed != null && pressed.isUp() && pressed.getOrder() < deck.getNCards()){
			cardPressed = pressed;
			cardLevelDown = deck.getLevelDown(pressed);
		}
		if (e.getButton() == 1){
			button1 = true;
			flashCard(cardPressed);
			for (int i = 0; i<cardLevelDown-1; i++) cardPressed.getCardFront().sendBackward();
		}
		if (e.getButton() == 3) flashCard(cardPressed);
	}
	
	
	public void mouseReleased(MouseEvent e){
		if (e.getButton() == 1){
			button1 = false;
			if(cardPressed.getOrder() < deck.getNCards()){
				GCompound onMe = (GCompound) getElementAt(cardPressed.getCardFront().getX() + (cardPressed.getCardFront().getWidth()/2), cardPressed.getCardFront().getY()-5);
				Card droppedOn = deck.getCard(onMe);
				if (droppedOn == null) dropCard(cardPressed, cardPressed.getCovers());
				else{
					while(deck.getLevelDown(droppedOn) != 1) droppedOn = droppedOn.getCovered();
					if(deck.isSuitDroppable(cardPressed, droppedOn) && deck.isNumDroppable(cardPressed, droppedOn)) dropCard(cardPressed, droppedOn);
					else if (droppedOn.getOrder() >= deck.getNCards()) dropCard(cardPressed, droppedOn);
					else dropCard(cardPressed, cardPressed.getCovers());
				}
			}
		}
		if (e.getButton() == 3){
			if (cardPressed != null){
				for(int i = 0; i<cardLevelDown - 1;i++) cardPressed.getCardFront().sendBackward();
			}
		}
			cardPressed = null;
	}
	
	public void mouseDragged(MouseEvent e){
		if (button1){
			if (cardPressed != null && cardPressed != deck.cards[deck.getNCards() + deck.getNSpaces()] && cardPressed.isUp()){
				cardPressed.getCovers().setCovered(null);
				for(int i = 0; i<deck.getLevelDown(cardPressed); i++){
					if (deck.getStack(cardPressed, i) != null && deck.getStack(cardPressed, i) != deck.cards[deck.getNCards() + deck.getNSpaces()] && deck.isSuitMoveable(cardPressed) && deck.isNumMoveable(cardPressed)){
						deck.getStack(cardPressed, i).getCardFront().move(e.getX() - last.getX(), e.getY() - last.getY());
					}
				}
				last = new GPoint (e.getPoint());
			}
		}
	}

	
	/**
	 * 
	 * Methods specific to Spider
	 * 
	 */
	
	private void checkForStack(Card card){
		int index = deck.getLevelDown(card) - 13;
		Card king = deck.getStack(card, index);
		if (king != null && king.getOrder() < deck.getNCards() && deck.isSuitMoveable(king) && deck.isNumMoveable(king)){
			removeStack(deck.getStack(card, index));
			deck.getStack(card, index).getCovers().setCovered(null);
			deck.getStack(card, index).setCovers(null);
			nStacks++;
		}
	}
	
// Removes all cards from "card" down
	private void removeStack(Card card){
		for (int i = 0; deck.getStack(card, i) != null; i++){
			deck.getStack(card, i).getCardFront().setLocation(5 + CARD_STACK_OFFSET*nStacks, getHeight() - card.getCardFront().getHeight() - 5);
			deck.getStack(card, i).setPlay(false);
		}
		
	}
	
	private void checkForWin(){
		if (nStacks == 4) gameOn = false;
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
			pause(.5);
			remove(banner);
		}
		add(banner);
		pause(750);
		for(double x = fx; x<getWidth(); x+=speed){
			add(banner, x, y);
			pause(.5);
			remove(banner);
		}
		add(banner, (getWidth() - banner.getWidth())/2, (getHeight() - banner.getHeight())/2);
	}
	
	
	
	
	
	
	
	
	
	
	
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
	
	
	private void flipCard(int index){
		double x, y;
		int down = deck.getLevelDown(deck.cards[index]) - 1;
		flashCard(deck.cards[index]);
		if(index != -1 && deck.cards[index].isUp()){
			x = deck.cards[index].getCardFront().getX();
			y = deck.cards[index].getCardFront().getY();
			remove(deck.cards[index].getCardFront());
			add(deck.cards[index].getCardBack(), x, y);
			deck.cards[index].setDirection(false);
			for (int i = 0; i < down; i++) deck.cards[index].getCardBack().sendBackward();
		}
		else if (index != -1 && !deck.cards[index].isUp()){
			x = deck.cards[index].getCardBack().getX();
			y = deck.cards[index].getCardBack().getY();
			remove(deck.cards[index].getCardBack());
			add(deck.cards[index].getCardFront(), x, y);
			deck.cards[index].setDirection(true);
			for (int i = 0; i < down; i++) deck.cards[index].getCardFront().sendBackward();
		}
		
	}
	
	private void flashCard(Card card){
		for (int i = -1*deck.getLevelUp(card); i<cardLevelDown; i++){
			if (deck.getStack(card, i) != null && deck.getStack(card, i).getOrder() <deck.getNCards() && deck.getStack(card, i).isUp()){
				getStack(card, i).getCardFront().sendToFront();
			}	
		}
		card.getCardFront().sendToFront();
		
	}
	
	private void dropCard(Card drop, Card on){
		double x = on.getCardFront().getX();
		double y = on.getCardFront().getY() + CARD_STACK_OFFSET;
		for (int i = 0; i < deck.getLevelDown(drop); i++){
			deck.getStack(drop, i).getCardFront().setLocation(x, y + (i*CARD_STACK_OFFSET));
		}
		drop.setCovers(on);
		on.setCovered(drop);
	}
	
	private GPoint last;
	private Card cardPressed;
	private int nStacks, cardLevelDown, index;
	private boolean button1, newGame, gameOn;
}
