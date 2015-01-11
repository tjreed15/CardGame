import acm.graphics.*;
import java.awt.event.*;
import java.awt.*;


public class Solitare extends CardDeck{
	
	private static final int CARD_STACK_OFFSET = 20;
	private static final int X_SEP = 4;
	private static final int INITIAL_X = 225;
	private static final int INITIAL_Y = 150;
	private static final int TOP_LINE = 20;
	private static final int DECK_X = 150;
	private static final int ACE_X = 650;
	
	private static final int NCARDS = 52;
	private static final int NCOLUMNS = 7;
	private static final int NSAFE_SPOTS = 0;
	private static final int NACE_SPOTS = 4;
	private static final int NDECK_SPOTS = 2;
	private static final int NSPACES = NCOLUMNS + NSAFE_SPOTS + NACE_SPOTS + NDECK_SPOTS;
	private static final int NFLIPPED_CARDS = 3;
	private static final int NMOVES_REM = 500;
	
// C - Color, S - Suit, A - Alternating Colors, Everything (else) - (Just) Number Value, Movable
	private static final char MOVE = 'A';
// C - Color, S - Suit, A - Alternating Colors, Everything (else) - (Just) Number Value, Dropable
	private static final char DROP = 'A';

	private CardDeck deck;
	private char move, drop;
	private boolean gameOn, replay, newGame;
	private int index;
	private int[][] moves = new int [NMOVES_REM] [2];
	private int CardsMoved;
	
	private GLabel test;
	private int testX, testY;
	
	
	public void run(){
		deck = new CardDeck(NCARDS, 0, NSPACES);
		deck.cards = deck.shuffled(deck.cards, NCARDS);
		replay = true;
		addMouseListeners();
		addKeyListeners();
		while(replay){
			initializeVariables();
			setUp();
			while (gameOn){
				checkForWin();
				if (replay || newGame) break;
			}
			if (!replay){
				if (!newGame) youWin();
				deck.cards = deck.shuffled(deck.cards, NCARDS);
			}
			askForReplay();
			removeAll();
			for (int i = 0; i < deck.cards.length - 1; i++){
				resetCard(deck.getCard(i));
			}
			
		}
	}
	
	private void initializeVariables(){
		test = new GLabel("TEST", 50, 50);
		testX = 50;
		testY = 500;
		
		hint1 = null;
		hint2 = null;
		CardsMoved = -1;
		for (int i = 0; i<NMOVES_REM; i++){
			moves[i] [0] = -1;
			moves[i][1] = -1;
		}
		replay = false;
		newGame = false;
		move = MOVE;
		drop = DROP;
		index = 0;
		gameOn = true;
	}
	
	public void setUp(){
		for (int j = 0; j<NCOLUMNS; j++){
			addColumn(j);
		}
		for (int i = 0; i< NACE_SPOTS; i++){
			add(deck.getCard((deck.getNCards() + NCOLUMNS + NSAFE_SPOTS) + i).getCardFront(), ACE_X + (deck.getCard(0).getCardFront().getWidth() + X_SEP)*i, TOP_LINE);
			deck.getCard((deck.getNCards() + NCOLUMNS + NSAFE_SPOTS) + i).setSpecial("Ace");
		}
		addDeck();
	}
	
	
	/**
	 * 
	 * Mouse Methods used in Solitare
	 * 
	 */
	
	
// But 1: Flips card over/Deals next hand
// But 3: ---
	public void mouseClicked(MouseEvent e){
		GCompound it = (GCompound) getElementAt(e.getX(), e.getY());
		Card clicked = (it == null)? null:deck.getCard(it);
		if (e.getButton() == 1){
			if (clicked != null && deck.isDeckDealer(clicked)){
				resetDeck();
			}
			else {
				Card base = deck.getStack(clicked, deck.getLevelDown(clicked) - 1);
				if (base != null && deck.isDeckDealer(base)){
					dealNextHand(clicked);
				}
				else if (clicked != null && !clicked.isUp() && clicked.getCovered() == null){
					flip(clicked);
					CardsMoved++;
					moves[CardsMoved][0] = clicked.getOrder();
					moves[CardsMoved][1] = -1;
					//addTest();
				}
			}
		}
		else if (e.getButton() == 3){
			sendAllMovesUp();
		}
	}
	
// Always: Resets card Pressed and its level down
// But 1: Brings stack to front Graphically (compared to other stacks)... button1--> true for drag purposes
// But 3: Flashes Card
	public void mousePressed(MouseEvent e){
		last = new GPoint (e.getPoint());
		if (hint1 != null) hint1.getCardFront().setColor(Color.BLACK);
		hint1 = null;
		if (hint2 != null) hint2.getCardFront().setColor(Color.BLACK);
		hint2 = null;
		hintNumber = 0;
		GCompound it = (GCompound) getElementAt(e.getX(), e.getY());
		Card pressed = (it == null)? null:deck.getCard(it);
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
	
// But 1: Drops any held cards...button1--> false for drag purposes
// But 3: Returns a "flashed" card to its place
	public void mouseReleased(MouseEvent e){
		if (e.getButton() == 1){
			button1 = false;
			if(!cardPressed.isSpace() && isMoveable(cardPressed)){
				Card droppedOn = null;
				GCompound onMe = (GCompound) getElementAt(cardPressed.getCardFront().getX() + (cardPressed.getCardFront().getWidth()/2), cardPressed.getCardFront().getY()-5);
				if (onMe != null){
					droppedOn = deck.getCard(onMe);
					while(deck.getLevelDown(droppedOn) != 1) droppedOn = droppedOn.getCovered();
					if(isDroppable(cardPressed, droppedOn)){
						CardsMoved++;
						moves[CardsMoved][0] = cardPressed.getCovers().getOrder();
						moves[CardsMoved][1] = cardPressed.getOrder();
						//addTest();
							
						cardPressed.setSpecial("");
						dropCard(cardPressed, droppedOn);
						if (deck.isOnAceSpot(droppedOn) || deck.isAceSpot(droppedOn)){
							cardPressed.setSpecial("OnAce");
							if (!deck.isAceSpot(droppedOn))cardPressed.getCardFront().move(0, -CARD_STACK_OFFSET);
						}
					}
					else if (droppedOn.getOrder() >= deck.getNCards() && !deck.isInDeck(droppedOn) && !deck.isDeckDealer(droppedOn) && !deck.isAceSpot(droppedOn)){
						CardsMoved++;
						moves[CardsMoved][0] = cardPressed.getCovers().getOrder();
						moves[CardsMoved][1] = cardPressed.getOrder();
						//addTest();
						
						cardPressed.setSpecial("");
						dropCard(cardPressed, droppedOn);
					}
					else{
						if (deck.isInDeck(cardPressed)){
							dropCard(cardPressed, cardPressed.getCovers());
							if (!cardPressed.getCovers().isSpace())cardPressed.getCardFront().move(CARD_STACK_OFFSET, -CARD_STACK_OFFSET);
						}
						else {
							cardPressed.setSpecial("");
							dropCard(cardPressed, cardPressed.getCovers());
						}
					}
				}
				else{
					if (deck.isInDeck(cardPressed)){
						dropCard(cardPressed, cardPressed.getCovers());
						if (!cardPressed.getCovers().isSpace())cardPressed.getCardFront().move(CARD_STACK_OFFSET, -CARD_STACK_OFFSET);
					}
					else{
						cardPressed.setSpecial("");
						dropCard(cardPressed, cardPressed.getCovers());
					}
				}
			}
		}
		if (e.getButton() == 3){
			if (cardPressed != null){
				for(int i = 0; i<cardLevelDown - 1; i++) cardPressed.getCardFront().sendBackward();
			}
		}
			cardPressed = null;
	}
	
// But 1: Drags any cards selected
// But 3: ---
	public void mouseDragged(MouseEvent e){
		if (button1){
			if (cardPressed != null && cardPressed.getOrder() < deck.getNCards() && cardPressed.isUp() && isMoveable(cardPressed)){
				if (cardPressed.getCovers() != null)cardPressed.getCovers().setCovered(null);
				for(int i = 0; i<deck.getLevelDown(cardPressed); i++){
					if (deck.getStack(cardPressed, i) != null && deck.getStack(cardPressed, i).getOrder() < deck.getNCards()){
						deck.getStack(cardPressed, i).getCardFront().move(e.getX() - last.getX(), e.getY() - last.getY());
					}
				}
				last = new GPoint (e.getPoint());
			}
		}
	}
	
	/**
	 * 
	 * KeyBoard Methods for Solitare
	 * 
	 */
	
	public void keyTyped(KeyEvent e){
		if (e.getKeyChar() == KeyEvent.VK_R){
			replay = true;
		}
		else if (e.getKeyChar() == KeyEvent.VK_Z){
			if (CardsMoved < NMOVES_REM && CardsMoved>=0) undoLastMove();
		}
		else if (e.getKeyChar() == KeyEvent.VK_SPACE){
			giveHint();
		}
		else if (e.getKeyChar() == KeyEvent.VK_N){
			newGame = true;
		}
		/**
		else if (e.getKeyChar() == KeyEvent.VK_SPACE){
			giveHint();
		}
		else if (e.getKeyChar() == KeyEvent.VK_Z){
			undoLastMove();
		}
		else if (e.getKeyChar() == KeyEvent.VK_Y){
			if (CardsMoved <= NMOVES_REM) redoLastMove();
		}
		*/	
	}
	
	
// Undoes last move made
	private void undoLastMove(){
		if (moves[CardsMoved][1] != -1 && moves[CardsMoved][0] != -1){
			deck.getCardByOrder(moves[CardsMoved][1]).getCovers().setCovered(null);
			flashCard(deck.getCardByOrder(moves[CardsMoved][1]));
			for (int i = 0; i<deck.getLevelDown(deck.getCardByOrder(moves[CardsMoved][1]))-1; i++) deck.getCardByOrder(moves[CardsMoved][1]).getCardFront().sendBackward();
			dropCard(deck.getCardByOrder(moves[CardsMoved][1]), deck.getCardByOrder(moves[CardsMoved][0]));
			deck.getCardByOrder(moves[CardsMoved][1]).setSpecial("");
			if (deck.isInDeck(deck.getCardByOrder(moves[CardsMoved][0]))){
				deck.getCardByOrder(moves[CardsMoved][1]).setSpecial("InDeck");
				if (!deck.getCardByOrder(moves[CardsMoved][0]).isSpace())deck.getCardByOrder(moves[CardsMoved][1]).getCardFront().move(CARD_STACK_OFFSET, -CARD_STACK_OFFSET);
			}
			if (deck.isOnAceSpot(deck.getCardByOrder(moves[CardsMoved][0]))){
				deck.getCardByOrder(moves[CardsMoved][1]).setSpecial("OnAce");
				deck.getCardByOrder(moves[CardsMoved][1]).getCardFront().move(0, -CARD_STACK_OFFSET);
			}
		}
		else if (moves[CardsMoved][1] == -1 && moves[CardsMoved][0] != -1){
			flip(deck.getCardByOrder(moves[CardsMoved][0]));
		}
		else if(moves[CardsMoved][0] == -1 && moves[CardsMoved][1] != -1){
			undoDeckMove();
			//addTest();
		}
		else if (moves[CardsMoved][0] == -1 && moves[CardsMoved][1] == -1);
		if (CardsMoved >= 0)CardsMoved--;
		
		
		
	}
	
	
	
	
	
	
	private void undoDeckMove(){
		double x = DECK_X + deck.getCard(0).getCardFront().getWidth() + (3*CARD_STACK_OFFSET/2);
		GCompound deckHolder;
		Card holder;
	// Sets all dealt cards into 1 single pile	
		for (int i = 0; i<NFLIPPED_CARDS;  i++){
			deckHolder = (GCompound) getElementAt(x + i*CARD_STACK_OFFSET +1, TOP_LINE + 5);
			holder = (deckHolder == null) ? null : deck.getCard(deckHolder); 
			if (holder != null && !holder.isSpace()) holder.getCardFront().setLocation(x, TOP_LINE);
		}
		deckHolder = (GCompound) getElementAt(x, TOP_LINE + 5);
		holder = (deckHolder == null) ? null : deck.getCard(deckHolder);
	// "Un" resets deck
		if (moves[CardsMoved][1]==0){
			GCompound topCard = (GCompound) getElementAt(DECK_X + 5, TOP_LINE + 5);
			Card inDeck = (topCard == null)? null : deck.getCard(topCard);
			while (inDeck != null && !inDeck.isSpace()){
				flip(inDeck);
				inDeck.getCardFront().sendToFront();
				inDeck.getCardFront().setLocation(x - (CARD_STACK_OFFSET/2), TOP_LINE);
			//Gets next card in deck to continue the for loop
				topCard = (GCompound) getElementAt(DECK_X + 5, TOP_LINE + 5);
				inDeck = (topCard == null)? null : deck.getCard(topCard);
			}
			deckHolder = (GCompound) getElementAt(x, TOP_LINE + 5);
			holder = (deckHolder == null) ? null : deck.getCard(deckHolder); 
			holder.setCovered(null);
		}
	// Undoes one set of flipped cards
		else{
			for (int i = 0; i<moves[CardsMoved][1]; i++){
				if (holder != null && !holder.isSpace()){
					flip(holder);
					holder.getCardBack().sendToFront();
					GCompound topCard = (GCompound) getElementAt(DECK_X + 5, TOP_LINE + 5);
					Card inDeck = (topCard == null)? null : deck.getCard(topCard);
					holder.getCardBack().setLocation(DECK_X, TOP_LINE);
					holder.setCovered(inDeck);
					if (i == moves[CardsMoved][1]-1) holder.getCovers().setCovered(null);
				// Gets next set of cards for the for loop
					deckHolder = (GCompound) getElementAt(x, TOP_LINE + 5);
					holder = (deckHolder == null)? null : deck.getCard(deckHolder);
				
				}
				
			}
		}
		
	// Moves deck to see top 3 cards
		int lastFlipIndex = -1;
		for (int i = 0; i<NMOVES_REM; i++){
			if (moves[i][0] == -1 && moves[i][1] > 0) lastFlipIndex = i;
		}
		for (int i=0; i<moves[lastFlipIndex][1]; i++){
			deckHolder = (GCompound) getElementAt(x , TOP_LINE + 5);
			holder = (deckHolder == null) ? null : deck.getCard(deckHolder); 
			if (holder != null && !holder.isSpace()) holder.getCardFront().move(CARD_STACK_OFFSET*(moves[lastFlipIndex][1]-1-i), 0);
		}
		
	}
	
	private void giveHint(){
		double x = INITIAL_X + 5 + hintNumber*(deck.getCard(0).getCardFront().getWidth() + X_SEP);
		GCompound c = (GCompound) getElementAt(x, INITIAL_Y + 5);
		Card cardToMove = deck.getCard(c);
		while (!cardToMove.isSpace() && !isMoveable(cardToMove)){
			cardToMove = deck.getStack(cardToMove, 1);
			if (deck.getLevelDown(cardToMove) == 1) break;
		}
		
		GRect tell = new GRect(x, INITIAL_Y + 500, 10, 10);
		add(tell);
		
		Card onCard = null;
		for (int i = 0; i<NCOLUMNS; i++){
			x = INITIAL_X + 5 + i*(deck.getCard(0).getCardFront().getWidth() + X_SEP);
			c = (GCompound) getElementAt(x, INITIAL_Y);
			onCard = deck.getCard(c);
			onCard = deck.getStack(onCard, deck.getLevelDown(onCard) - 1);
			if (isDroppable(cardToMove, onCard) || onCard.isSpace()){
				displayHint(cardToMove, onCard);
				break;
			}
		}
		if (!isDroppable(cardToMove, onCard) && !onCard.isSpace()){
			hintNumber++;
			if (hintNumber >= NCOLUMNS){
				hintNumber = 0;
			}
			else giveHint();
		}
		
	}
	
	private void displayHint(Card c1, Card c2){
		if (c1 == null || c2 == null){
			GRect noHint = new GRect(50, 50, 50, 50);
			add(noHint);
		}
		else{
			hint1 = c1;
			hint2 = c2;
			c1.getCardFront().setColor(Color.BLUE);
			c2.getCardFront().setColor(Color.BLUE);
		}
	}
	
	/**
	 * 
	 * Methods specific to Solitare
	 * 
	 */
	
	
	private void checkForWin(){
		boolean check = true;
		for (int i = 0; i<NCARDS; i++){
			if (!deck.isOnAceSpot(deck.getCard(i))){
				check = false;
				break;
			}
		}
		gameOn = !check;
	}
	
	private void sendAllMovesUp(){
		for (int i = 0; i<NCARDS; i++){
			if (deck.getCardByOrder(i).getValue() > 1){
				Card oneLess = deck.getCardByOrder(i - 1);
				if (deck.isOnAceSpot(oneLess) && deck.getCardByOrder(i).getCovered() == null && deck.getCardByOrder(i).isUp() && !deck.isOnAceSpot(deck.getCardByOrder(i))){
					CardsMoved++;
					moves[CardsMoved][0] = deck.getCardByOrder(i).getCovers().getOrder();
					moves[CardsMoved][1] = deck.getCardByOrder(i).getOrder();
					//addTest();
					
					deck.getCardByOrder(i).getCardFront().sendToFront();
					deck.getCardByOrder(i).getCardFront().setLocation(oneLess.getCardFront().getX(), oneLess.getCardFront().getY());
					oneLess.setCovered(deck.getCardByOrder(i));
					deck.getCardByOrder(i).getCovers().setCovered(null);
					deck.getCardByOrder(i).setCovers(oneLess);
					deck.getCardByOrder(i).setSpecial("OnAce");
					i = -1; //resets to look for any other moves made possible by one up
				}
			}
			else{
				if (deck.getCardByOrder(i).getCovered() == null && deck.getCardByOrder(i).isUp() && !deck.isOnAceSpot(deck.getCardByOrder(i))){
					Card open = null;
					double x = ACE_X;
					double y = TOP_LINE;
					do{
						GCompound spot = (GCompound) getElementAt(x, y);
						Card empty = (spot == null)? null:deck.getCard(spot);
						if (empty != null && empty.isSpace()){
							open = empty;
							break;
						}
						x += deck.getCard(0).getCardFront().getWidth();
					} 
					while (x<(ACE_X +(5*deck.getCard(0).getCardFront().getWidth())));
					
					CardsMoved++;
					moves[CardsMoved][0] = deck.getCardByOrder(i).getCovers().getOrder();
					moves[CardsMoved][1] = deck.getCardByOrder(i).getOrder();
					//addTest();
					
					deck.getCardByOrder(i).getCardFront().sendToFront();
					deck.getCardByOrder(i).getCardFront().setLocation(open.getCardFront().getX(), open.getCardFront().getY());
					open.setCovered(deck.getCardByOrder(i));
					deck.getCardByOrder(i).getCovers().setCovered(null);
					deck.getCardByOrder(i).setCovers(open);
					deck.getCardByOrder(i).setSpecial("OnAce");
					i = -1; //resets to look for any other moves made possible by one up
				}
			}
		}
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
	
	private void addDeck(){
		add(deck.getCard((deck.getNCards() + NCOLUMNS + NACE_SPOTS + NSAFE_SPOTS)).getCardFront(), DECK_X, TOP_LINE);
		deck.getCard((deck.getNCards() + NCOLUMNS + NACE_SPOTS + NSAFE_SPOTS)).setSpecial("Deal");
		deck.getCard((deck.getNCards() + NCOLUMNS + NACE_SPOTS + NSAFE_SPOTS)).setCovered(null);
		add(deck.getCard((deck.getNCards() + NCOLUMNS + NACE_SPOTS + NSAFE_SPOTS + 1)).getCardFront(), DECK_X +deck.getCard(0).getCardFront().getWidth() + CARD_STACK_OFFSET, TOP_LINE);
		deck.getCard((deck.getNCards() + NCOLUMNS + NACE_SPOTS + NSAFE_SPOTS + 1)).setSpecial("InDeck");
		deck.getCard((deck.getNCards() + NCOLUMNS + NACE_SPOTS + NSAFE_SPOTS + 1)).setCovered(null);
		deck.getCard((deck.getNCards() + NCOLUMNS + NACE_SPOTS + NSAFE_SPOTS + 1)).setCovers(null);
		while (index < NCARDS){
			GCompound down = (GCompound) getElementAt(DECK_X + 5, TOP_LINE + 5);
			Card covers = (down == null)? null:deck.getCard(down);
			if(covers != null){
				add(deck.getCard(index).getCardBack(), DECK_X, TOP_LINE);
				deck.getCard(index).setDirection(false);
				covers.setCovers(deck.getCard(index));
				deck.getCard(index).setCovered(covers);
				deck.getCard(index).setSpecial("InDeck");
			}
			index++;
		}
	}
	
	private void dealNextHand(Card clicked){
		double x = DECK_X + deck.getCard(0).getCardFront().getWidth() + (3*CARD_STACK_OFFSET/2);
		GCompound dealt;
		Card down = null; 
		for (int i = 0; i<NFLIPPED_CARDS;  i++){
			dealt = (GCompound) getElementAt(x + i*CARD_STACK_OFFSET, TOP_LINE + 5);
			down = (dealt == null) ? null : deck.getCard(dealt); 
			if (down != null && !down.isSpace()) down.getCardFront().setLocation(DECK_X +deck.getCard(0).getCardFront().getWidth() + CARD_STACK_OFFSET, TOP_LINE); 
		}
		int cardsFlipped = 0;
		for (int i = 0; i<NFLIPPED_CARDS; i++){
			if (deck.getStack(clicked, i) != null){
				if (deck.getStack(clicked, i).isSpace()){
					if (!deck.getStack(clicked, i-1).isSpace() && deck.getStack(clicked, i-1) != null)deck.getStack(clicked, i-1).setCovered(null);
					break;
				}
				if (i == 0 && down != null){
					deck.getStack(clicked, i).setCovers(down);
					down.setCovered(deck.getStack(clicked, i));
				}
				flip(deck.getStack(clicked, i));
				deck.getStack(clicked, i).getCardFront().sendToFront();
				deck.getStack(clicked, i).getCardFront().move(deck.getCard(0).getCardFront().getWidth() +((i+1)*(CARD_STACK_OFFSET)), 0);
				if (i == NFLIPPED_CARDS - 1) deck.getStack(clicked, i).setCovered(null);
				cardsFlipped++;
				
			}
		}
		CardsMoved++;
		moves[CardsMoved][0] = -1;
		moves[CardsMoved][1] = cardsFlipped;
		//addTest();

	}
	
	private void resetDeck(){
		double x = DECK_X + deck.getCard(0).getCardFront().getWidth() + (3*CARD_STACK_OFFSET/2);
		GCompound down;
		GCompound up;
		Card out = null;
		Card in = null;
		for (int i = 0; i<NFLIPPED_CARDS;  i++){
			down = (GCompound) getElementAt(x + i*CARD_STACK_OFFSET, TOP_LINE +5);
			out = (down == null) ? null : deck.getCard(down); 
			if (out != null && !out.isSpace()) out.getCardFront().setLocation(DECK_X +deck.getCard(0).getCardFront().getWidth() + CARD_STACK_OFFSET, TOP_LINE);
		} 
		do{
			down = (GCompound) getElementAt(x, TOP_LINE +5);
			up = (GCompound) getElementAt(DECK_X + 5, TOP_LINE + 5);
			out = (down == null) ? null : deck.getCard(down);
			in = (up == null) ? null : deck.getCard(up);
			if (out != null && !out.isSpace()){
				out.setCovered(in);
				in.setCovers(out);
				flip(out);
				out.getCardBack().sendToFront();
				out.getCardBack().setLocation(DECK_X, TOP_LINE);
			}
		}
		while (out != null && !out.isSpace());
		down = (GCompound) getElementAt(x, TOP_LINE +5);
		in = (up == null) ? null : deck.getCard(up);
		in.setCovers(null);
		
		CardsMoved++;
		moves[CardsMoved][0] = -1;
		moves[CardsMoved][1] = 0;
		//addTest();
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
	
	private void askForReplay(){
		removeAll();
		GLabel banner = new GLabel("Press 'R' To Play Again");
		add(banner, (getWidth() - banner.getWidth())/2, (getHeight() - banner.getHeight())/2);
		while (true){
			if (replay) break;
		}
	}
	
	/**
	 * 
	 * General methods to use for a deck
	 * 
	 */
	
	private boolean isMoveable(Card card){
		boolean isMoveable;
		if(move == 'C') isMoveable = (deck.isColorMoveable(card) && deck.isNumMoveable(card));
		else if(move == 'A') isMoveable = (deck.isAltMoveable(card) && deck.isNumMoveable(card));
		else if(move == 'S') isMoveable = (deck.isSuitMoveable(card) && deck.isNumMoveable(card));
		else isMoveable = (deck.isNumMoveable(card));
		isMoveable = isMoveable && card.isUp();
		return isMoveable;
	}
	
	private boolean isDroppable(Card card, Card droppedOn){
		boolean isDroppable;
		Card bot = deck.getStack(droppedOn, -1*deck.getLevelUp(droppedOn) + 1);
		if (isAceSpot(bot)){
			boolean goodOnSpace = true;
			if (droppedOn.isSpace() && card.getValue() != 1) goodOnSpace = false;
			isDroppable = (deck.isSuitDroppable(card, droppedOn) && deck.isIncNumDroppable(card, droppedOn));
			isDroppable = isDroppable && (deck.getLevelDown(card) == 1);
			isDroppable = isDroppable && goodOnSpace;
			isDroppable = isDroppable || (droppedOn.isSpace() && card.getValue() == 1);
		}
		else{
			if(drop == 'C') isDroppable = (deck.isColorDroppable(card, droppedOn) && deck.isNumDroppable(card, droppedOn));
			else if(drop == 'A') isDroppable = (deck.isAltDroppable(card, droppedOn) && deck.isNumDroppable(card, droppedOn));
			else if(drop == 'S') isDroppable = (deck.isSuitDroppable(card, droppedOn) && deck.isNumDroppable(card, droppedOn));
			else isDroppable = deck.isNumDroppable(card, droppedOn);
			isDroppable = isDroppable && !deck.isInDeck(droppedOn);
		}
		return isDroppable;
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
	
// Places card(and its stack) to bottom of pile if the top center of the card is placed anywhere near it
	private void dropCard(Card drop, Card on){
		double x, y;
		if (on.isUp()){
			x = on.getCardFront().getX();
			y = on.getCardFront().getY() + CARD_STACK_OFFSET;
		}
		else {
			x = on.getCardBack().getX();
			y = on.getCardBack().getY() + CARD_STACK_OFFSET;
		}
		//Card bot = deck.getStack(on, -1*deck.getLevelUp(on) + 1);
		for (int i = 0; i < deck.getLevelDown(drop); i++){
			if (on.isSpace())deck.getStack(drop, i).getCardFront().setLocation(x, y+ ((i -1)*CARD_STACK_OFFSET));
			/**
			else if (deck.isAceSpot(bot)){
				add(test);
				deck.getStack(drop, i).getCardFront().setLocation(x, y+ ((i -1)*CARD_STACK_OFFSET));
			}
			*/
			else deck.getStack(drop, i).getCardFront().setLocation(x, y + (i*CARD_STACK_OFFSET));
		}
		drop.setCovers(on);
		on.setCovered(drop);
	}
	
	private void addTest(){
		test = new GLabel("" + 1, testX, testY);
		add(test);
		if (testX < 1100){
			testX += 35;
		}
		else{
			testX = 50;
			testY += 25;
		}
		
	}
	
// Instance variables
	private GPoint last;
	private Card cardPressed;
	private int cardLevelDown;
	private boolean button1;
	private int hintNumber;
	private Card hint1, hint2;
	
}
