package com.flair.bi;

import com.flair.bi.domain.ViewState;
import com.flair.bi.domain.Visualization;
import com.flair.bi.domain.field.Field;
import com.flair.bi.domain.property.Property;
import com.flair.bi.domain.visualmetadata.BodyProperties;
import com.flair.bi.domain.visualmetadata.TitleProperties;
import com.flair.bi.domain.visualmetadata.VisualMetadata;
import com.project.bi.query.expression.condition.impl.AndConditionExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TestDataGenerator {

    public static ViewState createViewState() {
        VisualMetadata visualMetadata = createVisualMetadata();

        Set<VisualMetadata> visualMetadataSet = new HashSet<>();
        visualMetadataSet.add(visualMetadata);

        ViewState viewState = new ViewState();
        viewState.setVisualMetadataSet(visualMetadataSet);
        viewState.setReadOnly(true);
        return viewState;
    }

    public static VisualMetadata createVisualMetadata() {
        HashSet<Field> fields = new HashSet<>();
        HashSet<Property> properties = new HashSet<>();
        VisualMetadata visualMetadata = new VisualMetadata();
        visualMetadata.setId(UUID.randomUUID().toString());
        Visualization metadataVisual = new Visualization();
        metadataVisual.setId((long) (Math.random() * 100000));
        visualMetadata.setMetadataVisual(metadataVisual);
        visualMetadata.setCompositeId(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        visualMetadata.setFields(fields);
        visualMetadata.setHeight((int) (Math.random() * 10000));
        visualMetadata.setBodyProperties(new BodyProperties());
        visualMetadata.setConditionExpression(new AndConditionExpression());
        visualMetadata.setIsCardRevealed(true);
        visualMetadata.setIsSaved(true);
        visualMetadata.setProperties(properties);
        visualMetadata.setQuery("select * from mytable");
        visualMetadata.setQueryJson("select * from jsonquery");
        visualMetadata.setTitleProperties(new TitleProperties());
        visualMetadata.setWidth((int) (Math.random() * 10000));
        visualMetadata.setxPosition((int) (Math.random() * 10000));
        visualMetadata.setyPosition((int) (Math.random() * 10000));
        return visualMetadata;
    }
}
