// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.escavador.prelude.searcher;

import com.google.inject.Inject;
import com.yahoo.log.LogLevel;
import com.yahoo.component.ComponentId;
import com.yahoo.component.chain.dependencies.After;
import com.yahoo.component.chain.dependencies.Provides;
import com.yahoo.search.result.Hit;
import com.yahoo.search.Query;
import com.yahoo.search.Result;
import com.yahoo.container.QrSearchersConfig;
import com.yahoo.prelude.fastsearch.FastHit;
import com.yahoo.prelude.hitfield.FieldPart;
import com.yahoo.prelude.hitfield.HitField;
import com.yahoo.prelude.hitfield.ImmutableFieldPart;
import com.yahoo.prelude.hitfield.StringFieldPart;
import com.yahoo.search.Searcher;
import com.yahoo.search.searchchain.Execution;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

/**
 *
 */
@After(com.yahoo.prelude.searcher.JuniperSearcher.JUNIPER_TAG_REPLACING)
@Provides(com.escavador.prelude.searcher.BypassTagSearcher.BYPASS_TAG_REPLACING)
public class BypassTagSearcher extends Searcher {

    @Inject
    public BypassTagSearcher(ComponentId id, QrSearchersConfig config) {
        super(id);
        this.boldOpenTag = config.tag().bold().open();
        this.boldCloseTag = config.tag().bold().close();
        this.separatorTag = config.tag().separator();
    }

    @Override
    public Result search(Query query, Execution execution) {
        Result result = execution.search(query);

        String queryVespa = query.getModel().getQueryString();
        boolean isBolding = query.getPresentation().getBolding();
        Iterator<Hit> hitsToProcess = result.hits().deepIterator();
        IndexFacts indexFacts = execution.context().getIndexFacts();

        if (indexFacts != null)
            processHits(queryVespa, isBolding, hitsToProcess, null, indexFacts.newSession(query));

        return result;
    }

    private void processHits(String queryVespa, boolean isBolding, Iterator<Hit> hitsToProcess,
                                 String summaryClass, IndexFacts.Session indexFacts)
    {
        //In processing
    }

    private boolean hasHTMLTags(String text){
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public static final String BYPASS_TAG_REPLACING = "BypassTagReplacing";
    // The name of the field containing document type
    private static final String MAGIC_FIELD = Hit.SDDOCNAME_FIELD;
    private static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    private Pattern pattern = Pattern.compile(HTML_PATTERN);

    private String boldOpenTag;
    private String boldCloseTag;
    private String separatorTag;

    private static Logger log = Logger.getLogger(BypassTagSearcher.class.getName());
}
