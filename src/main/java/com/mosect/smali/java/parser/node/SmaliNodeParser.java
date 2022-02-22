package com.mosect.smali.java.parser.node;

import com.mosect.smali.java.parser.SmaliParseError;
import com.mosect.smali.java.parser.SmaliParseResult;
import com.mosect.smali.java.parser.SmaliToken;
import com.mosect.smali.java.parser.SmaliTokenTreeNode;

import java.util.List;

public class SmaliNodeParser {

    public SmaliParseResult<TreeDocument> parseDocument(SmaliParseResult<SmaliTokenTreeNode> treeResult) {
        return parseDocument(treeResult.getText(), treeResult.getResult());
    }

    public SmaliParseResult<TreeDocument> parseDocument(CharSequence rawText, SmaliTokenTreeNode tree) {
        SmaliParseResult<TreeDocument> result = new SmaliParseResult<>();
        TreeDocument document = new TreeDocument();
        result.setResult(document);
        result.setText(rawText);

        return result;
    }

//    public boolean parseWhitespaceAndComment(SmaliTokenTreeNode tree, SmaliTree parent, List<SmaliParseError> outErrors) {
//        if (null == tree.getChildren()) {
//            SmaliToken token = tree.getToken();
//        } else {
//            for (SmaliTokenTreeNode token : tree.getChildren()) {
//
//            }
//        }
//    }
}
