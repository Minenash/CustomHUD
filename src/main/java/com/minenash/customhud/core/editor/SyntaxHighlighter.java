package com.minenash.customhud.core.editor;

import org.fife.ui.rsyntaxtextarea.*;

import javax.swing.text.Segment;
import java.util.Arrays;

import static com.minenash.customhud.core.editor.LineParseContext.*;

public class SyntaxHighlighter extends AbstractTokenMaker {

    @Override
    public TokenMap getWordsToHighlight() {
        return new TokenMap();
    }

    @Override
    public Token getTokenList(Segment segment, int startTokenType, int startOffset) {
        resetTokenList();

        if (segment.count == 0) {
            addNullToken();
            return firstToken;
        }



        char[] chars = segment.array;
        int sOffset = segment.offset;
        int sEnd = segment.offset+segment.count-1;


        int tokenStart = sOffset;


        if (chars.length >= 2 && chars[sOffset] == '/' && chars[sOffset+1] == '/')
            addToken(segment, sOffset, sEnd, CToken.COMMENT, startOffset);
        else if (chars[sOffset] == '=') {
            LineParseContext context = THEME_PREFIX;
            boolean isIf = false;
            for (int i = sOffset; i <= sEnd; i++) {
                if (context == THEME_PREFIX && (chars[i] != '=' || i-tokenStart > 1)) {
                    addToken(segment, tokenStart, i-1, CToken.SYNTAX, tokenStart-sOffset+startOffset);
                    tokenStart = i;
                    context = THEME_KEY;
                }
                if (context == THEME_KEY && chars[i] == ':') {
                    String left = new String(Arrays.copyOfRange(chars, tokenStart, i));
                    if (left.equalsIgnoreCase("if") || left.equalsIgnoreCase("endif"))
                        isIf = true;
                    addToken(segment, tokenStart, i-1, isIf ? CToken.SYNTAX : CToken.THEME_KEY, tokenStart-sOffset+startOffset);
                    addToken(segment, i, i, CToken.SYNTAX, i-sOffset+startOffset);
                    i++;
                    tokenStart = i;
                    context = THEME_VALUE;
                }
                if ((context == THEME_KEY || context == THEME_VALUE) && chars[i] == '=') {
                    boolean isEndIf = new String(Arrays.copyOfRange(chars, tokenStart, i)).equalsIgnoreCase("endif");
                    addToken(segment, tokenStart, i-1,
                            isEndIf ? CToken.SYNTAX :
                            context == THEME_KEY ? CToken.THEME_KEY :
                            isIf ? CToken.CONDITIONAL :
                            CToken.THEME_VALUE, tokenStart-sOffset+startOffset);
                    tokenStart = i;
                    context = THEME_POSTFIX;
                }
                if (context == THEME_POSTFIX && i-tokenStart > 1) {
                    addToken(segment, tokenStart, i-1, CToken.SYNTAX, tokenStart-sOffset+startOffset);
                    tokenStart = i;
                    context = NORMAL;
                }
            }
            if (tokenStart <= sEnd)
                addToken(segment, tokenStart, sEnd, switch (context) {
                    case THEME_KEY -> CToken.THEME_KEY;
                    case THEME_VALUE -> isIf ? CToken.CONDITIONAL : CToken.THEME_VALUE;
                    case NORMAL -> CToken.NORMAL;
                    default -> CToken.SYNTAX;
                }, tokenStart-sOffset+startOffset);

        }
        else {
            int tokenType = CToken.NORMAL;
            LineParseContext context = NORMAL;
            for (int i = sOffset; i <= sEnd; i++) {
                if (chars[i] == '&' && i+1 <= sEnd && isColorCode(chars[i+1])) {
                    addToken(segment, tokenStart, i-1, tokenType, tokenStart-sOffset+startOffset);
                    addToken(segment, i, i+1, CToken.COLOR, i-sOffset+startOffset);
                    tokenStart = i+2;
                    i++;
                }
                if (chars[i] == '{' && !(i+1 > sEnd || chars[i+1] == '{')) {
                    addToken(segment, tokenStart, i-1, tokenType, tokenStart-sOffset+startOffset);
                    addToken(segment, i, i, CToken.SYNTAX, i-sOffset+startOffset);
                    tokenStart = i+1;
                    tokenType = CToken.VARIABLE;
                    context = VARIABLE_INNER;
                }
                if (context == VARIABLE_INNER && chars[i] == ' ') {
                    addToken(segment, tokenStart, i-1, tokenType, tokenStart-sOffset+startOffset);
                    tokenStart = i;
                    tokenType = CToken.FLAG;
                    context = VARIABLE_FLAGS;

                }
                if (chars[i] == '{' && i+1 <= sEnd && chars[i+1] == '{') {
                    addToken(segment, tokenStart, i-1, tokenType, tokenStart-sOffset+startOffset);
                    addToken(segment, i, i+1, CToken.SYNTAX, i-sOffset+startOffset);
                    tokenStart = i+2;
                    i++;
                    tokenType = CToken.CONDITIONAL;
                    context = CONDITIONAL_PHRASE;
                }
                if (context == CONDITIONAL_PHRASE && chars[i] == ',') {
                    addToken(segment, tokenStart, i-1, tokenType, tokenStart-sOffset+startOffset);
                    addToken(segment, i, i, CToken.SYNTAX, i-sOffset+startOffset);
                    tokenStart = i+1;
                    context = CONDITIONAL_SYNTAX;
                }
                if (context == CONDITIONAL_INNER && chars[i] == '"') {
                    addToken(segment, tokenStart, i-1, tokenType, tokenStart-sOffset+startOffset);
                    addToken(segment, i, i, CToken.SYNTAX, i-sOffset+startOffset);
                    tokenStart = i+1;
                    tokenType = CToken.CONDITIONAL;
                    context = CONDITIONAL_PHRASE;
                }
                if (context == CONDITIONAL_SYNTAX && chars[i] == '"') {
                    addToken(segment, tokenStart, i-1, tokenType, tokenStart-sOffset+startOffset);
                    addToken(segment, i, i, CToken.SYNTAX, i-sOffset+startOffset);
                    tokenStart = i+1;
                    tokenType = CToken.NORMAL;
                    context = CONDITIONAL_INNER;
                }
                if (chars[i] == '}') {
                    int j = i+1 <= sEnd && chars[i+1] == '}' ? i+1 : i;
                    addToken(segment, tokenStart, i-1, tokenType, tokenStart-sOffset+startOffset);
                    addToken(segment, i, j, CToken.SYNTAX, i-sOffset+startOffset);
                    tokenStart = j+1;
                    i = j;
                    tokenType = CToken.NORMAL;
                    if (context != CONDITIONAL_SYNTAX)
                        context = NORMAL;
                }
            }
            if (tokenStart <= sEnd)
                addToken(segment, tokenStart, sEnd, tokenType, tokenStart-sOffset+startOffset);

        }


        addNullToken();
        return firstToken;
    }

    public static boolean isColorCode(char code) {
        return (code >= '0' && code <= '9') || (code >= 'A' && code <= 'Z') ||(code >= 'a' && code <= 'z');
    }
}
