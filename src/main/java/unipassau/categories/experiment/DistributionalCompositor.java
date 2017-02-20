package unipassau.categories.experiment;

import org.indra_project.common.client.Language;
import org.indra_project.common.client.Model;
import org.indra_project.common.client.ScoreFunction;
import org.indra_project.common.client.TextPair;
import org.indra_project.rest.resources.RelatednessRequest;
import org.indra_project.rest.resources.RelatednessResource;
import org.indra_project.rest.resources.RelatednessResponse;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;

import java.util.*;

public class DistributionalCompositor {

    public static final String ALPHARD_INDRA = "http://alphard.fim.uni-passau.de:8916/indra/v1/relatedness";

    public static final String CHUTZPAH_INDRA = "http://132.231.141.167:8916/indra/v1/relatedness";

    public String serverAdd = CHUTZPAH_INDRA;

    protected Language lang;

    protected Model model;

    public Set<String> fullCategoriesSet;

    public Map<Integer, List<String>> fullCategoriesBlock;

    public DistributionalCompositor(String lang, String model) {
        this.lang = Language.valueOf(lang.toUpperCase());
        this.model = (model != null ? Model.valueOf(model.toUpperCase()) : Model.W2V);
    }

    public LinkedHashMap<String, Double> relatedness(String query) {
        LinkedHashMap<String, Double> results = new LinkedHashMap<>();

        for (Integer id : fullCategoriesBlock.keySet()) {
            System.out.println("processing block number " + id);
            List<String> block = fullCategoriesBlock.get(id);
            List<TextPair> pairsToScore = new ArrayList<>(block.size());

            block.forEach(d -> {
                pairsToScore.add(new TextPair(query, d));
            });

            RelatednessRequest request = createRequest(pairsToScore);

            Context context = new Context();
            context.getParameters().add("readTimeout", "0");
            Client client = new Client(context, Protocol.HTTP);
            ClientResource service = new ClientResource(serverAdd);
            service.setNext(client);
            service.setRetryOnError(false);

            RelatednessResource resource = service.wrap(RelatednessResource.class);
            RelatednessResponse response = resource.getRelatedness(request);

            response.pairs.forEach(p -> results.put(p.t2, Math.abs(p.score)));

        }

        return results;
    }

    public boolean hasCategory(String category) {
        return this.fullCategoriesSet.contains(category);
    }

    private RelatednessRequest createRequest(List<TextPair> pairs) {
        RelatednessRequest request = new RelatednessRequest();
        request.corpus = "wiki-2014";
        request.language = this.lang;
        request.model = this.model;
        request.scoreFunction = ScoreFunction.COSINE;
        request.pairs = pairs;

        return request;
    }

    @Override
    public String toString() {
        return "DistributionalCompositor [lang: " + this.lang + " | model: " + model + "]";
    }
}
