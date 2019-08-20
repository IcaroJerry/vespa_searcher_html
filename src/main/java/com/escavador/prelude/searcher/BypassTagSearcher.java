// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.escavador.prelude.searcher;

import com.google.inject.Inject;
import com.yahoo.component.ComponentId;
import com.yahoo.component.chain.dependencies.Before;
import com.yahoo.component.chain.dependencies.Provides;
import com.yahoo.search.result.Hit;
import com.yahoo.search.Query;
import com.yahoo.search.Result;
import com.yahoo.container.QrSearchersConfig;
import com.yahoo.prelude.fastsearch.FastHit;
import com.yahoo.prelude.hitfield.HitField;
import com.yahoo.search.Searcher;
import com.yahoo.search.searchchain.Execution;
import com.yahoo.prelude.Index;
import com.yahoo.prelude.IndexFacts;
import com.yahoo.search.result.HitGroup;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

/**
 *
 */
@Before(com.yahoo.prelude.searcher.JuniperSearcher.JUNIPER_TAG_REPLACING)
@Provides(com.escavador.prelude.searcher.BypassTagSearcher.BYPASS_TAG_REPLACING)
public class BypassTagSearcher extends Searcher
{

    @Inject
    public BypassTagSearcher(ComponentId id, QrSearchersConfig config)
    {
        super(id);
    }

    @Override
    public Result search(Query query, Execution execution)
    {
        Result result = execution.search(query);

        String queryVespa = query.getModel().getQueryString();
        boolean isBolding = query.getPresentation().getBolding();
        IndexFacts indexFacts = execution.context().getIndexFacts();

        if (indexFacts != null)
            this.processHits(result, indexFacts.newSession(result.getQuery()));

        return result;
    }

    private void processHits(Result result, IndexFacts.Session indexFacts)
    {
        HitGroup hits = new HitGroup();
        Iterator<Hit> hitsToProcess = result.hits().deepIterator();
        while (hitsToProcess.hasNext())
        {
            Hit hit = hitsToProcess.next();

            if (!(hit instanceof FastHit)) continue;

            FastHit fastHit = (FastHit) hit;

            Object searchDefinitionField = fastHit.getField(MAGIC_FIELD);
            if (searchDefinitionField == null) continue;

            for (Index index : indexFacts.getIndexes(searchDefinitionField.toString()))
            {
                HitField field;
                if (index.hasCommand("bypass_tag"))
                {
                    field = fastHit.buildHitField(index.getName(), true);
                    if (field == null) continue;
                }
                else continue;

                String documentToProcess = field.getContent();
                if(hasHTMLTags(documentToProcess)) continue;

                hits.add(hit);
            }
        }

        result.setHits(hits);
    }

    private boolean hasHTMLTags(String text)
    {
        pattern = Pattern.compile(HTML_PATTERN);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public static final String BYPASS_TAG_REPLACING = "BypassTagReplacing";
    // The name of the field containing document type
    private static final String MAGIC_FIELD = Hit.SDDOCNAME_FIELD;
    private static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    private Pattern pattern = Pattern.compile(HTML_PATTERN);
    private static Logger log = Logger.getLogger(BypassTagSearcher.class.getName());
}
