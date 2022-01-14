package net.dirtcraft.commons.text;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

import static net.dirtcraft.commons.text.Colors.*;
import static net.dirtcraft.commons.text.Colors.DARK_RED;
import static net.dirtcraft.commons.text.Styles.PADDING;
import static net.dirtcraft.commons.text.Styles.PADDING_CH;

public class Window {
    protected List<IFormattableTextComponent> text;

    protected Window(){}

    public Window(String title, List<IFormattableTextComponent> text) {
        this.text = generatePage(getHeader(title), text);
    }

    private List<IFormattableTextComponent> generatePage(IFormattableTextComponent header, List<IFormattableTextComponent> text){
        List<IFormattableTextComponent> content = new ArrayList<>();
        IFormattableTextComponent component = new StringTextComponent("").append(header);
        for (IFormattableTextComponent line : text) component.append(line);
        component.append(getFooter());
        content.add(component);
        return content;
    }

    protected IFormattableTextComponent getHeader(String title) {
        String[] titleText = (Styles.padCenter("\\" + title + "\\", 38, PADDING_CH) + "\n").split("\\\\");
        return new StringTextComponent("")
                .append(new StringTextComponent(titleText[0]).withStyle(PADDING))
                .append(new StringTextComponent(String.format(" %s ", titleText[1])).withStyle(Styles.as(WHITE)))
                .append(new StringTextComponent(titleText[2]).withStyle(PADDING));
    }

    private IFormattableTextComponent getFooter() {
        int padding = (34);
        String padText = String.format("%" + padding + "s", "").replace(' ', '-');
        return new StringTextComponent("")
                .append(new StringTextComponent(padText).withStyle(Styles.as(DARK_RED).setStrikethrough(true)));
    }

    public IFormattableTextComponent getPage(int page){
        return text.get(0);
    }
}
