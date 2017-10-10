package com.tblf.instrumentation.sourcecode.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

import java.util.Collection;

public class TargetProcessor extends AbstractProcessor<CtStatement> {

    private Collection targets;

    public TargetProcessor(Collection<String> targets) {
        super();
        this.targets = targets;
    }

    @Override
    public void process(CtStatement ctStatement) {
        CtClass ctType = ctStatement.getParent(CtClass.class);

        //Is a target
        if (ctType != null && targets.contains(ctType.getQualifiedName())) {
            instrumentStatement(ctStatement);
        }
    }

    /**
     * Insert a Statement before each existing statement, in order to trace the execution of all the statements of the code during the execution
     * @param ctStatement
     */
    private void instrumentStatement(CtStatement ctStatement) {
        if (!ctStatement.isImplicit() && !(ctStatement instanceof CtBlock) && !(ctStatement instanceof CtCodeSnippetStatement)) {
            //The ctStatement is not a super(). we can insert an other just before it
            CtCodeSnippetStatement ctCodeSnippetStatement = getFactory().Core().createCodeSnippetStatement();
            // source end +1 to match MoDisco
            ctCodeSnippetStatement.setValue(String.format("com.tblf.Link.Calls.match(\"%s\", \"%s\")", ctStatement.getPosition().getSourceStart(), ctStatement.getPosition().getSourceEnd()+1));
            ctStatement.insertBefore(ctCodeSnippetStatement);
        }
    }
}
