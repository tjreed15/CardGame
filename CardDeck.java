import acm.graphics.*;
import acm.util.RandomGenerator;

public class CardDeck extends Card {
	
	public Card[] cards;
	private int NCARDS;
	private int NSPACES;
	private int NJOKERS;
	
// Modifier: Declares deck of 52 cards and 13 spaces
	public CardDeck(){
		NCARDS = 52;
		NJOKERS = 0;
		NSPACES = 17;
		cards = cards();
	}
	
//Modifier: Declares deck of nCards cards and nSpaces spaces
	public CardDeck(int nCards, int nJokers, int nSpaces){
		NCARDS = nCards;
		NJOKERS = nJokers;
		NSPACES = nSpaces;
		cards = cards();
	}
	
	public CardDeck(int nCards){
		NCARDS = nCards;
		NJOKERS = 0;
		NSPACES = 0;
		cards = numericallyOrdered();
	}
	
// Adds cards to public array with card "value" at corresponding array index.. saves one extra card space to hold any insignificant information
	public Card[] cards(){
		Card[] cards = new Card[NCARDS + NSPACES + NJOKERS + 1];
		for (int i = 0; i<NCARDS; i++){
			cards[i]= new Card(i%52, false);
		}
		for(int i = 0; i<NJOKERS; i++){
			cards[NCARDS + i] = new Card(52, false);
		}
		for (int i = 0; i<NSPACES; i++){
			cards[NCARDS + NJOKERS + i] = new Card(53 + i, true);
		}
		return cards;
	}
	
	public Card[] numericallyOrdered(){
		Card[] cards = new Card[NCARDS + 1];
		for (int i = 0; i<NCARDS; i++) cards[i]= new Card(((i%4)*13) + i/4, false);
		return cards;
	}
	
	public Card[] shuffled(Card[] cards, int nCards){
		Card[] shuffled = new Card[cards.length];
		for(int i = 0; i < nCards; i++){
			int index = rgen.nextInt(0, nCards-1);
			if(shuffled[index] == null)shuffled[index] = cards[i];
			else i--;
		}
		for (int i = nCards; i<cards.length; i++){
			shuffled[i] = cards[i];
		}
		return shuffled;
	}

	public void resetCard(Card card){
		card.setDirection(true);
		card.setPlay(true);
		card.setCovered(null);
		card.setCovers(null);
		card.setSpecial("");
	}
	
	
	
	/**
	 * 
	 * Methods involved in CardDeck
	 * 
	 */

// Returns number of cards in deck
	public int getNCards(){
		return NCARDS;
	}

// Returns number of "spaces" in deck
	public int getNSpaces(){
		return NSPACES;
	}
	
// Returns number of jokers in deck
	public int getNJokers(){
		return NJOKERS;
	}
	
// Returns the card in the array index "index"
	public Card getCard(int index){
		return cards[index];
	}
	
// Returns the card (usually after a click) that is represented by a front/back of the card. returns last space in array otherwise
	public Card getCard(GCompound clicked){
		for (int i = 0; i<NCARDS+NSPACES+NJOKERS; i++){
			if (clicked == cards[i].getCardFront() || clicked == cards[i].getCardBack()){
				return cards[i];
			}
		}
		return null;
	}
	
	public Card getCardByOrder(int order){
		for (int i = 0; i<NCARDS+NSPACES+NJOKERS; i++){
			if (order == cards[i].getOrder()){
				return cards[i];
			}
		}
		return null;

	}
	
// Returns true/false if card is/is not a card
	public boolean isCard(GCompound guess){
		for (int i = 0; i<NCARDS + NJOKERS; i++){
			if(cards[i].getCardFront() == guess || cards[i].getCardBack() == guess){
				return true;
			}
		}
		return false;
	}
	
// Returns true/false if card is/is not a "space"
	public boolean isSpace(GCompound guess){
		for (int i = NCARDS + NJOKERS; i<NCARDS + NJOKERS + NSPACES; i++){
			if(cards[i].getCardFront() == guess || cards[i].getCardBack() == guess){
				return true;
			}
		}
		return false;
	}
	
// Returns the Graphical level of the card in a stack, 1 being on top
	public int getLevelDown(Card card){
		if (card.getCovered() == null) return 1;
		else return (getLevelDown(card.getCovered())) + 1;
	}
	
// Returns the Graphical level of the card in a stack, 1 being on bottom
	public int getLevelUp(Card card){
		if (card.getCovers() == null) return 1;
		else return (getLevelUp(card.getCovers())) + 1;
	}
	
// Returns the Card "index" distance away from given card in stack (negative is covered by card, positive is covering card)
	public Card getStack(Card card, int index){
		if (index == 0) return card;
		else if (index > 0) return getStack(card.getCovered(), index-1);
		else return getStack(card.getCovers(), index + 1);
	}
	
// Returns if the card is used as AceHolder
	public boolean isAceSpot(Card card){
		if (card.getSpecial().equalsIgnoreCase("Ace")) return true;
		else return false;
	}
	
// Returns if the card is Up on Ace
	public boolean isOnAceSpot(Card card){
		if (card.getSpecial().equalsIgnoreCase("OnAce")) return true;
		else return false;
	}
	
// Returns if the card is used as UpSpace
	public boolean isUpSpace(Card card){
		if (card.getSpecial().equalsIgnoreCase("Up")) return true;
		else return false;
	}
	
// Returns if the card is used as DeckDealer
	public boolean isDeckDealer(Card card){
		if (card.getSpecial().equalsIgnoreCase("Deal")) return true;
		else return false;
	}
	
// Returns if the card is in the deck
	public boolean isInDeck(Card card){
		if (card.getSpecial().equalsIgnoreCase("InDeck")) return true;
		else return false;
	}
	
//	Sets the number of cards in the deck (I don't know when this should ever be used)
	public void setNCards(int nCards){
		NCARDS = nCards;
	}
	
// Sets the number of spaces in the deck (I don't know when this should ever be used)
	public void setNSpaces(int nSpaces){
		NSPACES = nSpaces;
	}
	
// Sets the number of jokers in the deck (I don't know when this should ever be used)
	public void setNJokers(int nJokers){
		NJOKERS = nJokers;
	}
	
// Determines if the stack from the card down is facing up, and decreasing in number
	public boolean isNumMoveable(Card card){
		if (card.isUp()){
			if (getLevelDown(card) == 1) return true;
			else if (card.getValue() == card.getCovered().getValue() + 1) return isNumMoveable(card.getCovered());
			else return false;
		}
		else return false;
	}
	
// Determines if the stack from the card down is facing up, and alternating each color
	public boolean isAltMoveable(Card card){
		if (card.isUp()){
			if (getLevelDown(card) == 1) return true;
			else if (card.getOrder() >= 26 && card.getCovered().getOrder() < 26) return isAltMoveable(card.getCovered());
			else if (card.getOrder() < 26 && card.getCovered().getOrder() >= 26) return isAltMoveable(card.getCovered());
			else return false;
		}
		else return false;
	}
	
// Determines if the stack from the card down is facing up, and the same color
	public boolean isColorMoveable(Card card){
		if (card.isUp()){
			if (getLevelDown(card) == 1) return true;
			else if (card.getOrder() >= 26 && card.getCovered().getOrder() >= 26) return isColorMoveable(card.getCovered());
			else if (card.getOrder() < 26 && card.getCovered().getOrder() < 26) return isColorMoveable(card.getCovered());
			else return false;
		}
		else return false;
	}
	
// Determines if the stack from the card down is facing up, and the same suit
	public boolean isSuitMoveable(Card card){
		if (card.isUp()){
			if (getLevelDown(card) == 1) return true;
			else if (card.getOrder() >= 0 && card.getCovered().getOrder() >= 0 && card.getOrder() < 13 && card.getCovered().getOrder() < 13) return isSuitMoveable(card.getCovered());
			else if (card.getOrder() >= 13 && card.getCovered().getOrder() >= 13 && card.getOrder() < 26 && card.getCovered().getOrder() < 26) return isSuitMoveable(card.getCovered());
			else if (card.getOrder() >= 26 && card.getCovered().getOrder() >= 26 && card.getOrder() < 39 && card.getCovered().getOrder() < 39) return isSuitMoveable(card.getCovered());
			else if (card.getOrder() >= 39 && card.getCovered().getOrder() >= 39 && card.getOrder() < 52 && card.getCovered().getOrder() < 52) return isSuitMoveable(card.getCovered());
			else return false;
		}
		else return false;	
	}
	
// Determines if the card "on" is exactly 1 greater than the card "drop"
	public boolean isNumDroppable(Card drop, Card on){
		if (drop.isUp() && on.isUp() && drop.getValue() == on.getValue() - 1) return true;
		else return false;
	}
	
// Determines if the card "on" is exactly 1 less than the card "drop"
	public boolean isIncNumDroppable(Card drop, Card on){
		if (drop.isUp() && on.isUp() && drop.getValue() == on.getValue() + 1) return true;
		else return false;
	}
	
// Determines if the card "on" is the opposite color of the card "drop"
	public boolean isAltDroppable(Card drop, Card on){
		if (drop.isUp() && on.isUp()){
			if (drop.getOrder() >= 26 && on.getOrder() < 26) return true;
			else if (drop.getOrder() < 26 && on.getOrder() >= 26) return true;
			else return false;
		}
		else return false;
	}
	
// Determines if the card "on" is the same color as the card "drop"
	public boolean isColorDroppable(Card drop, Card on){
		if (drop.isUp() && on.isUp()){
			if (drop.getOrder() >= 26 && on.getOrder() >= 26) return true;
			else if (drop.getOrder() < 26 && on.getOrder() < 26) return true;
			else return false;
		}
		else return false;
	}
	
// Determines if the card "on" is the same suit as the card "drop"
	public boolean isSuitDroppable(Card drop, Card on){
		if (drop.isUp() && on.isUp()){
			if (drop.getOrder() >= 0 && on.getOrder() >= 0 && drop.getOrder() < 13 && on.getOrder() < 13)return true;
		else if (drop.getOrder() >= 13 && on.getOrder() >= 13 && drop.getOrder() < 26 && on.getOrder() < 26) return true;
		else if (drop.getOrder() >= 26 && on.getOrder() >= 26 && drop.getOrder() < 39 && on.getOrder() < 39) return true;
		else if (drop.getOrder() >= 39 && on.getOrder() >= 39 && drop.getOrder() < 52 && on.getOrder() < 52) return true;
		else return false;
		}
		else return false;
	}
	
/**
 * 
 * Methods that must be copied into the sub-class
 * Uses a CardDeck name of deck
 * Don't forget to addMouseListeners();

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
		if(pressed.isUp() && pressed.getOrder() < deck.getNCards()){
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
	private int cardLevelDown;
	private boolean button1;
*/	

	
	/**Private instance variable for random number generator */
	private RandomGenerator rgen = RandomGenerator.getInstance();

	
}
