/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.model.serializer;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.OwnedModelItem;
import de.erdesignerng.util.XMLUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class AbstractXMLModelSerializer extends CommonAbstractXMLSerializer {

    protected static final String CONFIGURATION = "Configuration";

    protected static final String DIALECT = "dialect";

    protected static final String CUSTOMTYPES = "CustomTypes";

    protected static final String DOMAINS = "Domains";

    protected static final String TABLES = "Tables";

    protected static final String RELATIONS = "Relations";

    protected static final String VIEWS = "Views";

    protected static final String SUBJECTAREAS = "Subjectareas";

    protected static final String COMMENTS = "Comments";

    private AbstractXMLAttributeSerializer xmlAttributeSerializer = null;

    private AbstractXMLCommentSerializer xmlCommentSerializer = null;

    private AbstractXMLDomainSerializer xmlDomainSerializer = null;

    private AbstractXMLCustomTypeSerializer xmlCustomTypeSerializer = null;

    private AbstractXMLIndexSerializer xmlIndexSerializer = null;

    private AbstractXMLRelationSerializer xmlRelationSerializer = null;

    private AbstractXMLSubjectAreaSerializer xmlSubjectAreaSerializer = null;

    private AbstractXMLTableSerializer xmlTableSerializer = null;

	private AbstractXMLViewSerializer xmlViewSerializer = null;

    private XMLUtils utils = null;

    protected AbstractXMLModelSerializer(XMLUtils aUtils) {
        utils = aUtils;
    }

    /**
     * Test if the persister supports a document version.
     *
     * @param aDocument the document
     * @return true if yes, else false
     */
    public boolean supportsDocument(Document aDocument) {
        NodeList theNodes = aDocument.getElementsByTagName(MODEL);
        if (theNodes.getLength() != 1) {
            return false;
        }
        Element theDocumentElement = (Element) theNodes.item(0);
        return getVersion().equals(theDocumentElement.getAttribute(VERSION));
    }

    public Model deserializeModelFromXML(Document aDocument) throws SAXException, IOException {

        if (!supportsDocument(aDocument)) {
            throw new IOException("Unsupported model version");
        }

        final List<SAXParseException> theExceptions = new ArrayList<>();

        // Validate the document
        SchemaFactory theSchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // hook up org.XML.sax.ErrorHandler implementation.
        ErrorHandler theHandler = new ErrorHandler() {

            @Override
            public void error(SAXParseException aException) {
                theExceptions.add(aException);
            }

            @Override
            public void fatalError(SAXParseException aException) {
                theExceptions.add(aException);
            }

            @Override
            public void warning(SAXParseException aException) {
                theExceptions.add(aException);
            }
        };
        theSchemaFactory.setErrorHandler(theHandler);

        // get the custom xsd schema describing the required format for my XML
        // files.
        Schema theSchema = theSchemaFactory.newSchema(getClass().getResource(getSchemaResource()));

        // Create a Validator capable of validating XML files according to my
        // custom schema.
        Validator validator = theSchema.newValidator();
        validator.setErrorHandler(theHandler);

        // parse the XML DOM tree against the stricter XSD schema
        validator.validate(new DOMSource(aDocument));

        if (theExceptions.size() > 0) {
            throw new IOException("Failed to validate document against schema", theExceptions.get(0));
        }

        return deserialize(aDocument);
    }

    public void serializeModelToXML(Model aModel, Writer aWriter) throws IOException, TransformerException {

        Document theDocument = utils.newDocument();

        serialize(aModel, theDocument);

        utils.transform(theDocument, aWriter);

        aWriter.close();
    }

    protected abstract Model deserialize(Document aDocument);

    protected abstract void serialize(Model aModel, Document aDocument);

    @Override
    @Deprecated
    public void serialize(OwnedModelItem aModelItem, Document aDocument, Element aRootElement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Deprecated
    public void deserialize(Model aModel, Document aDocument) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected abstract String getVersion();

    protected abstract String getSchemaResource();

    public AbstractXMLAttributeSerializer getXMLAttributeSerializer() {
        return xmlAttributeSerializer;
    }

    protected AbstractXMLCommentSerializer getXMLCommentSerializer() {
        return xmlCommentSerializer;
    }

    protected AbstractXMLCustomTypeSerializer getXMLCustomTypeSerializer(AbstractXMLModelSerializer xmlModelSerializer) {
        return xmlCustomTypeSerializer;
    }

    protected AbstractXMLDomainSerializer getXMLDomainSerializer() {
        return xmlDomainSerializer;
    }

    public AbstractXMLIndexSerializer getXMLIndexSerializer() {
        return xmlIndexSerializer;
    }

    protected AbstractXMLRelationSerializer getXMLRelationSerializer() {
        return xmlRelationSerializer;
    }

    protected AbstractXMLSubjectAreaSerializer getXMLSubjectAreaSerializer() {
        return xmlSubjectAreaSerializer;
    }

    protected AbstractXMLTableSerializer getXMLTableSerializer(AbstractXMLModelSerializer xmlModelSerializer) {
        return xmlTableSerializer;
    }

	protected AbstractXMLViewSerializer getXMLViewSerializer() {
        return xmlViewSerializer;
    }

    protected void setXMLAttributeSerializer(AbstractXMLAttributeSerializer xmlAttributeSerializer) {
        this.xmlAttributeSerializer = xmlAttributeSerializer;
    }

    protected void setXMLCommentSerializer(AbstractXMLCommentSerializer xmlCommentSerializer) {
        this.xmlCommentSerializer = xmlCommentSerializer;
    }

    protected void setXMLCustomTypeSerializer(AbstractXMLCustomTypeSerializer xmlCustomTypeSerializer) {
        this.xmlCustomTypeSerializer = xmlCustomTypeSerializer;
    }

    protected void setXMLDomainSerializer(AbstractXMLDomainSerializer xmlDomainSerializer) {
        this.xmlDomainSerializer = xmlDomainSerializer;
    }

    protected void setXMLIndexSerializer(AbstractXMLIndexSerializer xmlIndexSerializer) {
        this.xmlIndexSerializer = xmlIndexSerializer;
    }

    protected void setXMLRelationSerializer(AbstractXMLRelationSerializer xmlRelationSerializer) {
        this.xmlRelationSerializer = xmlRelationSerializer;
    }

    protected void setXMLSubjectAreaSerializer(AbstractXMLSubjectAreaSerializer xmlSubjectAreaSerializer) {
        this.xmlSubjectAreaSerializer = xmlSubjectAreaSerializer;
    }

    protected void setXMLTableSerializer(AbstractXMLTableSerializer xmlTableSerializer) {
        this.xmlTableSerializer = xmlTableSerializer;
    }

	protected void setXMLViewSerializer(AbstractXMLViewSerializer xmlViewSerializer) {
        this.xmlViewSerializer = xmlViewSerializer;
    }
}