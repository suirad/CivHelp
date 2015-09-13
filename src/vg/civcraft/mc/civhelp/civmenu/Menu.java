package vg.civcraft.mc.civhelp.civmenu;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.TextComponent;

public class Menu {
	TextComponent title;
	TextComponent subTitle;
	List<TextComponent> parts;
	
	public Menu(){
		title = new TextComponent("");
		subTitle = new TextComponent("");
		parts = new ArrayList<TextComponent>();
	}
	
	/**
	 * Returns the title
	 * @return Returns the TextComponent of the title.
	 */
	public TextComponent getTitle() {
		return title;
	}

	public void setTitle(TextComponent title) {
		this.title = title;
	}

	public TextComponent getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(TextComponent subTitle) {
		this.subTitle = subTitle;
	}

	public List<TextComponent> getParts() {
		return parts;
	}

	public void addPart(TextComponent part){
		parts.add(part);
	}
	
	public void setParts(TextComponent... parts){
		for(TextComponent part:parts){
			this.parts.add(part);
		}
	}
	
	public TextComponent create(){
		TextComponent menu = new TextComponent(title);
		menu.addExtra("\n");
		
		if(subTitle.getText()!= ""){
			menu.addExtra(subTitle);
			menu.addExtra("\n");
		}
		
		for(int i = 0; i<parts.size(); i++){
			menu.addExtra(parts.get(i));
			if(i != parts.size() - 1){
				menu.addExtra(", ");
			}
		}
		
		return menu;
	}
}
