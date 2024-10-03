package de.fernunihagen.dbis.anguillasearch;

import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;


public class NetworkGraph {
    mxGraphModel model;
    mxCircleLayout layout;
    mxGraph graph;
    Crawler crawler;

    public NetworkGraph(Crawler crawler) {
        this.model = new mxGraphModel();
        this.graph = new mxGraph(model);
        this.crawler = crawler;
        this.layout = new mxCircleLayout(graph);

        var parent = graph.getDefaultParent();
        this.setStyle();
        Map<String, Object> vertices = new HashMap<>();
        model.beginUpdate();
        for (String url : crawler.getUrls()) {
            Object cell = graph.insertVertex(parent, url, url, 0, 0, 100, 30, "ROUNDED");
            //graph.updateCellSize(cell, true);
            vertices.put(url, cell);
        }
        for (Crawler.Link link : crawler.getLinksWithin()) {
            graph.insertEdge(parent, null, "", vertices.get(link.source), vertices.get(link.target));
        }
        layout.execute(graph.getDefaultParent());
        for (Object cell : vertices.values()) {
            graph.updateCellSize(cell, true);
        }
        model.endUpdate();
    }

    public void setStyle() {
        mxStylesheet stylesheet = this.graph.getStylesheet();
        HashMap<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_OPACITY, 50);
        style.put(mxConstants.STYLE_FONTCOLOR, "#774400");
        //style.put(mxConstants.STYLE_FONTSIZE, 100);
        stylesheet.getDefaultVertexStyle().put(mxConstants.STYLE_FONTSIZE, 20);
        stylesheet.putCellStyle("ROUNDED", style);
    }

    public void draw(String filePath) throws IOException{
        //this.setStyle();
        BufferedImage img = mxCellRenderer.createBufferedImage(graph, null, 1, null, false, null);
        ImageIO.write(img, "png", new File(filePath));
    }
}
