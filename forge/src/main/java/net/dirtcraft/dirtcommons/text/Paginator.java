package net.dirtcraft.commons.text;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

import static net.dirtcraft.commons.text.Colors.*;
import static net.dirtcraft.commons.text.Styles.PADDING;
import static net.dirtcraft.commons.text.Styles.PADDING_CH;

public class Paginator extends Window {
    private final int elementsPerPage;
    private final int pages;

    public Paginator(String title, List<IFormattableTextComponent> text, int elementsPerPage, String command) {
        this.elementsPerPage = elementsPerPage;
        this.pages = (int) Math.ceil((double)Math.max(text.size(), 1) / elementsPerPage);
        this.text = generatePage(getHeader(title), text, command);
    }

    private List<IFormattableTextComponent> generatePage(IFormattableTextComponent header, List<IFormattableTextComponent> text, String command){
        List<IFormattableTextComponent> content = new ArrayList<>();
        IFormattableTextComponent component = new StringTextComponent("")
                .append(header);
        int i = 0;
        for (IFormattableTextComponent line : text) {
            if (i++ == elementsPerPage) {
                component.append(getFooter(content.size() + 1, command));
                content.add(component);
                component = new StringTextComponent("")
                        .append(header);
                i = 0;
            }
            component.append(line);
        }
        component.append(getFooter(content.size() + 1, command));
        content.add(component);
        return content;
    }

    protected IFormattableTextComponent getFooter(int page, String command) {
        String pageCount = String.format(" %d/%d ", page, pages);
        int padding = (35 - 4) - pageCount.length();
        String padText = String.format("%" + padding/2 + "s", "").replace(' ', '-');
        IFormattableTextComponent back = new StringTextComponent(" «").withStyle(page == 1? Styles.as(RED): Styles.withHoverAndClickCmd(WHITE, "Click to go back!", String.format("%s %d", command, page - 1)));
        IFormattableTextComponent forward = new StringTextComponent("» ").withStyle(page == pages? Styles.as(RED): Styles.withHoverAndClickCmd(WHITE, "Click to go to the next page!", String.format("%s %d", command, page + 1)));
        return new StringTextComponent("")
                .append(new StringTextComponent(padText).withStyle(Styles.as(DARK_RED).setStrikethrough(true)))
                .append(back)
                .append(new StringTextComponent(pageCount).withStyle(Styles.as(DARK_RED)))
                .append(forward)
                .append(new StringTextComponent(padText).withStyle(Styles.as(DARK_RED).setStrikethrough(true)));
    }

    public IFormattableTextComponent getPage(int page){
        if (page < 1) page = 1;
        if (page > pages) page = pages;
        return text.get(page - 1);
    }
}
