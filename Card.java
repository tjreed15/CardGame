import acm.graphics.*;
import acm.program.*;
import java.awt.*;

public class Card extends GraphicsProgram{

	private static final int CARD_WIDTH = 66;
	private static final int CARD_HEIGHT = 100;
	private static final Color CARD_COLOR = Color.RED;
	private static final String JOKER = "JOKER";
	private static final int SUIT_SIZE = 13;
	
	
	private boolean faceUp;
	private boolean inPlay;
	private boolean isSpace;
	private GCompound front;
	private GCompound back;
	private int value;
	private Card coveredBy;
	private Card covers;
	private String special;
	
	
	public Card(){
		faceUp = true;
		inPlay = true;
		isSpace = false;
		value = 0;
		coveredBy = null;
		covers = null;
		special = "";
	}
	
	public Card(int val, boolean space){
		value = val;
		faceUp = true;
		inPlay = true;
		isSpace = space;
		coveredBy = null;
		covers = null;
		special = "";
		
		GCompound card = new GCompound();
		GRoundRect base = new GRoundRect(CARD_WIDTH, CARD_HEIGHT);
		base.setFilled(true);
		base.setFillColor(Color.WHITE);
		card.add(base);
		if (val<13) addClub(card);
		else if (val<26) addSpade(card);
		else if(val<39) addDiamond(card);
		else if (val < 52) addHeart(card);
		else{}
		addValue(card, val);
		
		GCompound backs = new GCompound();
		GRoundRect backSide = new GRoundRect(CARD_WIDTH, CARD_HEIGHT);
		backSide.setFilled(true);
		backSide.setFillColor(CARD_COLOR);
		backs.add(backSide);
		
		front = card;
		back = backs;
	}

	public GCompound getCardFront(){
		return front;
	}
	
	public GCompound getCardBack(){
		return back;
	}
	
	public boolean isUp(){
		return faceUp;
	}
	
	public boolean inPlay(){
		return inPlay;
	}
	
	public boolean isSpace(){
		return isSpace;
	}
	
	public int getOrder(){
		return value;
	}
	
	public int getValue(){
		return (value % 13) + 1;
	}
	
	public Card getCovered(){
		return coveredBy;
	}
	
	public Card getCovers(){
		return covers;
	}
	
	public String getSpecial(){
		return special;
	}

	public void setCardFront(GCompound card){
		front = card;
	}
	
	public void setCardBack(GCompound card){
		back = card;
	}
	
	public void setDirection(boolean up){
		faceUp = up;
	}
	
	public void setPlay(boolean in){
		inPlay = in;
	}
	
	public void setSpace(boolean space){
		isSpace = space;
	}
	
	public void setValue(int val){
		value = val;
	}
	
	public void setCovered(Card card){
		coveredBy = card;
	}
	
	public void setCovers(Card card){
		covers = card;
	}
	
	public void setSpecial(String sp){
		special = sp;
	}

	
	/**
	 * 
	 * Methods for Graphics of Suits
	 * 
	 */
	private void addClub(GCompound card){
		GCompound  top = club(SUIT_SIZE, SUIT_SIZE, true);
		card.add(top, 3, 3);
		GCompound  bottom = club(SUIT_SIZE, SUIT_SIZE, false);
		card.add(bottom, CARD_WIDTH-21, CARD_HEIGHT-21);
	}
	
	private void addSpade(GCompound card){
		GCompound top = spade(SUIT_SIZE, SUIT_SIZE, true);
		card.add(top, 3, 3);
		GCompound bottom = spade(SUIT_SIZE, SUIT_SIZE, false);
		card.add(bottom, CARD_WIDTH - 21, CARD_HEIGHT - 21);
	}
	
	private void addDiamond(GCompound card){
		GCompound top = diamond(SUIT_SIZE, SUIT_SIZE);
		card.add(top, 3, 3);
		GCompound bottom = diamond(SUIT_SIZE, SUIT_SIZE);
		card.add(bottom, CARD_WIDTH - 21, CARD_HEIGHT - 21);
	}
	
	private void addHeart(GCompound card){
		GCompound top = heart(SUIT_SIZE, SUIT_SIZE, true, Color.RED);
		card.add(top, 3, 3);
		GCompound bottom = heart(SUIT_SIZE, SUIT_SIZE, false, Color.RED);
		card.add(bottom, CARD_WIDTH - 21, CARD_HEIGHT - 21);
	}
	
	
	private void addValue(GCompound card, int value){
		int cValue = value%13;
		GLabel num;
		switch(cValue){
			case 0: num = new GLabel("A"); break;
			case 10: num = new GLabel("J"); break;
			case 11: num = new GLabel("Q"); break;
			case 12: num = new GLabel("K"); break;
			default: num = new GLabel("" + (cValue+1)); break;
		}
		if (value == 52) num = new GLabel(JOKER);
		else if (value > 52) num = new GLabel("");
		num.setFont("SANS_SERIF-bold-15");
		double x = (CARD_WIDTH - num.getWidth())/2 ;
		double y = (num.getHeight());
		if (value >= 26) num.setColor(Color.RED);
		if (value >= 52)num.setColor(Color.BLACK);
		card.add(num, x, y);
	}
	
	
		private GCompound club(double width, double height, boolean up){
			GCompound club = new GCompound();
			GPolygon edges; 
			if(up){
				edges = new GPolygon(7*width/16, height);
				edges.addEdge(width/4, 0);
				edges.addEdge((-width/8) + 1, -height/3);
				edges.addArc(width/3, -height/3, 165, -270);
				edges.addArc(width/3, -height/3, 45, -270);
				edges.addArc(width/3, -height/3, 295, -270);
			}
			else{
				edges = new GPolygon(9*width/16, 0);
				edges.addEdge(-width/4, 0);
				edges.addEdge((width/8) - 1, height/3);
				edges.addArc(-width/3, height/3, 165, -270);
				edges.addArc(-width/3, height/3, 45, -270);
				edges.addArc(-width/3, height/3, 295, -270);
			}
			edges.setFilled(true);
			club.add(edges);
			return club;
		}	
		private GCompound spade(double width, double height, boolean up){
			GCompound spade = new GCompound();
			spade = heart(width, height, !up, Color.BLACK);
			GRect stem = new GRect(width/8, height/2);
			stem.setFilled(true);
			if (up)spade.add(stem, 7*width/16, 2*height/3);
			else spade.add(stem, 7*width/16, -(height/6));
			return spade;
		}
		private GCompound heart(double width, double height, boolean up, Color color){
			GCompound heart = new GCompound();
			GPolygon edges;
			if (up){
				edges  = new GPolygon(0, height/4);
				edges.addArc(width/2, height/2, 180, -180);
				edges.addArc(width/2, height/2, 180, -180);
				edges.addEdge(-width/2, 3*height/4);
				edges.addEdge(-width/2, -3*height/4);
			}
			else{
				edges  = new GPolygon(0, 3*height/4);
				edges.addArc(width/2, height/2, 180, 180);
				edges.addArc(width/2, height/2, 180, 180);
				edges.addEdge(-width/2, -3*height/4);
				edges.addEdge(-width/2, 3*height/4);
			}
			edges.setColor(color);
			edges.setFilled(true);
			heart.add(edges);
			return heart;
		}
		private GCompound diamond(double width, double height){
			GCompound diamond = new GCompound();
			GPolygon lines = new GPolygon(0, height/2);
			lines.addEdge(width/2, -height/2);
			lines.addEdge(width/2, height/2);
			lines.addEdge(-width/2, height/2);
			lines.addEdge(-width/2, -height/2);
			lines.setColor(Color.RED);
			lines.setFilled(true);
			diamond.add(lines);
			return diamond;
		}

}
