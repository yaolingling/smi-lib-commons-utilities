/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.commons.utilities;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Hobin_Lee
 *
 */
@XmlRootElement(name = "Link")
public class Link {
    private String title = null;
    private String href = null;
    private String rel = null;
    private String type = null;

    /**
     * Link relation types should be registered to IANA. Up-to-date registered link relations are listed in http://www.iana.org/assignments/link-relations/link-relations.xhtml
     */

    public enum RelationType {
        /**
         * Refers to a resource that is the subject of the link's context. [RFC6903], section 2
         */
        ABOUT("about"),
        /**
         * Refers to a substitute for this context [http://www.w3.org/TR/html5/links.html#link-type-alternate]
         */
        ALTERNATE("alternate"),
        /**
         * Refers to an appendix. [http://www.w3.org/TR/1999/REC-html401-19991224]
         */
        APPENDIX("appendix"),
        /**
         * Refers to a collection of records, documents, or other materials of historical interest. [http://www.w3.org/TR/2011/WD-html5-20110113/links.html#rel-archives]
         */
        ARCHIVES("archives"),
        /**
         * Refers to the context's author. [http://www.w3.org/TR/html5/links.html#link-type-author]
         */
        AUTHOR("author"),
        /**
         * Gives a permanent link to use for bookmarking purposes. [http://www.w3.org/TR/html5/links.html#link-type-bookmark]
         */
        BOOKMARK("bookmark"),
        /**
         * Designates the preferred version of a resource (the IRI and its contents). [RFC6596]
         */
        CANONICAL("canonical"),
        /**
         * Refers to a chapter in a collection of resources. [http://www.w3.org/TR/1999/REC-html401-19991224]
         */
        CHAPTER("chapter"),
        /**
         * The target IRI points to a resource which represents the collection resource for the context IRI. [RFC6573]
         */
        COLLECTION("collection"),
        /**
         * Refers to a table of contents. [http://www.w3.org/TR/1999/REC-html401-19991224]
         */
        CONTENTS("contents"),
        /**
         * Refers to a copyright statement that applies to the link's context. [http://www.w3.org/TR/1999/REC-html401-19991224]
         */
        COPYRIGHT("copyright"),
        /**
         * The target IRI points to a resource where a submission form can be obtained. [RFC6861]
         */
        CREATEFORM("create-form"),
        /**
         * Refers to a resource containing the most recent item(s) in a collection of resources. [RFC5005]
         */
        CURRENT("current"),
        /**
         * Refers to a resource providing information about the link's context. [http://www.w3.org/TR/powder-dr/#assoc-linking]
         */
        DESCRIBEDBY("describedby"),
        /**
         * The relationship A 'describes' B asserts that resource A provides a description of resource B. There are no constraints on the format or representation of either A or B,
         * neither are there any further constraints on either resource. [RFC6892] This link relation type is the inverse of the 'describedby' relation type. While 'describedby'
         * establishes a relation from the described resource back to the resource that describes it, 'describes' established a relation from the describing resource to the
         * resource it describes. If B is 'describedby' A, then A 'describes' B.
         */
        DESCRIBES("describes"),
        /**
         * Refers to a list of patent disclosures made with respect to material for which 'disclosure' relation is specified. [RFC6579]
         */
        DISCLOSURE("disclosure"),
        /**
         * Refers to a resource whose available representations are byte-for-byte identical with the corresponding representations of the context IRI. [RFC6249] This relation is
         * for static resources. That is, an HTTP GET request on any duplicate will return the same representation. It does not make sense for dynamic or POSTable resources and
         * should not be used for them.
         */
        DUPLICATE("duplicate"),
        /**
         * Refers to a resource that can be used to edit the link's context. [RFC5023]
         */
        EDIT("edit"),
        /**
         * The target IRI points to a resource where a submission form for editing associated resource can be obtained. [RFC6861]
         */
        EDITFORM("edit-form"),
        /**
         * Refers to a resource that can be used to edit media associated with the link's context. [RFC5023]
         */
        EDITMEDIA("edit-media"),
        /**
         * Identifies a related resource that is potentially large and might require special handling. [RFC4287]
         */
        ENCLOSURE("enclosure"),
        /**
         * An IRI that refers to the furthest preceding resource in a series of resources. [RFC5988] This relation type registration did not indicate a reference. Originally
         * requested by Mark Nottingham in December 2004.
         */
        FIRST("first"),
        /**
         * Refers to a glossary of terms. [http://www.w3.org/TR/1999/REC-html401-19991224]
         */
        GLOSSARY("glossary"),
        /**
         * Refers to context-sensitive help. [http://www.w3.org/TR/html5/links.html#link-type-help]
         */
        HELP("help"),
        /**
         * Refers to a resource hosted by the server indicated by the link context. [RFC6690] This relation is used in CoRE where links are retrieved as a "/.well-known/core"
         * resource representation, and is the default relation type in the CoRE Link Format.
         */
        HOSTS("hosts"),
        /**
         * Refers to a hub that enables registration for notification of updates to the context. [http://pubsubhubbub.googlecode.com] This relation type was requested by Brett
         * Slatkin.
         */
        HUB("hub"),
        /**
         * Refers to an icon representing the link's context. [http://www.w3.org/TR/html5/links.html#link-type-icon]
         */
        ICON("icon"),
        /**
         * Refers to an index. [http://www.w3.org/TR/1999/REC-html401-19991224]
         */
        INDEX("index"),
        /**
         * The target IRI points to a resource that is a member of the collection represented by the context IRI. [RFC6573]
         */
        ITEM("item"),
        /**
         * An IRI that refers to the furthest following resource in a series of resources. [RFC5988] This relation type registration did not indicate a reference. Originally
         * requested by Mark Nottingham in December 2004.
         */
        LAST("last"),
        /**
         * Points to a resource containing the latest (e.g., current) version of the context. [RFC5829]
         */
        LATESTVERSION("latest-version"),
        /**
         * Refers to a license associated with this context. [RFC4946] For implications of use in HTML, see: http://www.w3.org/TR/html5/links.html#link-type-license
         */
        LICENSE("license"),
        /**
         * Refers to further information about the link's context, expressed as a LRDD ("Link-based Resource Descriptor Document") resource. See [RFC6415] for information about
         * processing this relation type in host-meta documents. When used elsewhere, it refers to additional links and other metadata. Multiple instances indicate additional LRDD
         * resources. LRDD resources MUST have an "application/xrd+xml" representation, and MAY have others. [RFC6415]
         */
        LRDD("lrdd"),
        /**
         * Refers to a resource that can be used to monitor changes in an HTTP resource. [RFC5989]
         */
        MONITOR("monitor"),
        /**
         * Refers to a resource that can be used to monitor changes in a specified group of HTTP resources. [RFC5989]
         */
        MONITORGROUP("monitor-group"),
        /**
         * Indicates that the link's context is a part of a series, and that the next in the series is the link target. [http://www.w3.org/TR/html5/links.html#link-type-next]
         */
        NEXT("next"),
        /**
         * Refers to the immediately following archive resource. [RFC5005]
         */
        NEXTARCHIVE("next-archive"),
        /**
         * Indicates that the context's original author or publisher does not endorse the link target. [http://www.w3.org/TR/html5/links.html#link-type-nofollow]
         */
        NOFOLLOW("nofollow"),
        /**
         * Indicates that no referrer information is to be leaked when following the link. [http://www.w3.org/TR/html5/links.html#link-type-noreferrer]
         */
        NOREFERRER("noreferrer"),
        /**
         * Indicates a resource where payment is accepted. [RFC5988] This relation type registration did not indicate a reference. Requested by Joshua Kinberg and Robert Sayre. It
         * is meant as a general way to facilitate acts of payment, and thus this specification makes no assumptions on the type of payment or transaction protocol. Examples may
         * include a web page where donations are accepted or where goods and services are available for purchase. rel="payment" is not intended to initiate an automated
         * transaction. In Atom documents, a link element with a rel="payment" attribute may exist at the feed/channel level and/or the entry/item level. For example, a
         * rel="payment" link at the feed/channel level may point to a "tip jar" URI, whereas an entry/ item containing a book review may include a rel="payment" link that points
         * to the location where the book may be purchased through an online retailer.
         */
        PAYMENT("payment"),
        /**
         * Points to a resource containing the predecessor version in the version history. [RFC5829]
         */
        PREDECESSORVERSION("predecessor-version"),
        /**
         * Indicates that the link target should be preemptively cached. [http://www.w3.org/TR/html5/links.html#link-type-prefetch]
         */
        PREFETCH("prefetch"),
        /**
         * Indicates that the link's context is a part of a series, and that the previous in the series is the link target. [http://www.w3.org/TR/html5/links.html#link-type-prev]
         */
        PREV("prev"),
        /**
         * Refers to a resource that provides a preview of the link's context. [RFC6903], section 3
         */
        PREVIEW("preview"),
        /**
         * Refers to the previous resource in an ordered series of resources. Synonym for "prev". [http://www.w3.org/TR/1999/REC-html401-19991224]
         */
        PREVIOUS("previous"),
        /**
         * Refers to the immediately preceding archive resource. [RFC5005]
         */
        PREVARCHIVE("prev-archive"),
        /**
         * Refers to a privacy policy associated with the link's context. [RFC6903], section 4
         */
        PRIVACYPOLICY("privacy-policy"),
        /**
         * Identifying that a resource representation conforms to a certain profile, without affecting the non-profile semantics of the resource representation. [RFC6906] Profile
         * URIs are primarily intended to be used as identifiers, and thus clients SHOULD NOT indiscriminately access profile URIs.
         */
        PROFILE("profile"),
        /**
         * Identifies a related resource. [RFC4287]
         */
        RELATED("related"),
        /**
         * Identifies a resource that is a reply to the context of the link. [RFC4685]
         */
        REPLIES("replies"),
        /**
         * Refers to a resource that can be used to search through the link's context and related resources. [http://www.opensearch.org/Specifications/OpenSearch/1.1]
         */
        SEARCH("search"),
        /**
         * Refers to a section in a collection of resources. [http://www.w3.org/TR/1999/REC-html401-19991224]
         */
        SECTION("section"),
        /**
         * Conveys an identifier for the link's context. [RFC4287]
         */
        SELF("self"),
        /**
         * Indicates a URI that can be used to retrieve a service document. [RFC5023] When used in an Atom document, this relation type specifies Atom Publishing Protocol service
         * documents by default. Requested by James Snell.
         */
        SERVICE("service"),
        /**
         * Refers to the first resource in a collection of resources. [http://www.w3.org/TR/1999/REC-html401-19991224]
         */
        START("start"),
        /**
         * Refers to a stylesheet. [http://www.w3.org/TR/html5/links.html#link-type-stylesheet]
         */
        STYLESHEET("stylesheet"),
        /**
         * Refers to a resource serving as a subsection in a collection of resources. [http://www.w3.org/TR/1999/REC-html401-19991224]
         */
        SUBSECTION("subsection"),
        /**
         * Points to a resource containing the successor version in the version history. [RFC5829]
         */
        SUCCESSORVERSION("successor-version"),
        /**
         * Gives a tag (identified by the given address) that applies to the current document. [http://www.w3.org/TR/html5/links.html#link-type-tag]
         */
        TAG("tag"),
        /**
         * Refers to the terms of service associated with the link's context. [RFC6903], section 5
         */
        TERMSOFSERVICE("terms-of-service"),
        /**
         * Refers to a resource identifying the abstract semantic type of which the link's context is considered to be an instance. [RFC6903], section 6
         */
        TYPE("type"),
        /**
         * Refers to a parent document in a hierarchy of documents. [RFC5988] This relation type registration did not indicate a reference. Requested by Noah Slater.
         */
        UP("up"),
        /**
         * Points to a resource containing the version history for the context. [RFC5829]
         */
        VERSIONHISTORY("version-history"),
        /**
         * Identifies a resource that is the source of the information in the link's context. [RFC4287]
         */
        VIA("via"),
        /**
         * Points to a working copy for this resource. [RFC5829]
         */
        WORKINGCOPY("working-copy"),
        /**
         * Points to the versioned resource from which this working copy was obtained. [RFC5829]
         */
        WORKINGCOPYOF("working-copy-of");

        private final String name;


        private RelationType(String name) {
            this.name = name;
        }


        public String getName() {
            return name;
        }


        @Override
        public String toString() {
            return name;
        }
    }


    public Link() {
    }


    public Link(String title, String href, String rel, String type) {
        this.title = title;
        this.href = href;
        this.rel = rel;
        this.type = type;
    }


    public Link(String title, String href, String rel) {
        this(title, href, rel, null);
    }


    public Link(String title, String href, RelationType rel) {
        this(title, href, rel.getName(), null);
    }


    @XmlAttribute
    // @ApiModelProperty(value = "Label for the destination for the link in href", required = false)
    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    @XmlAttribute
    // @ApiModelProperty(value = "URI for the link", required = true)
    public String getHref() {
        return href;
    }


    public void setHref(String href) {
        this.href = href;
    }


    @XmlAttribute
    // @ApiModelProperty(value = "Relationship of resource to destination link", required = true)
    public String getRel() {
        return rel;
    }


    /**
     * Note that relations in Link is not an array. Concatenate with space delimiter. No comma is allowed. Eg NEXT FIRST
     *
     * @param rel the new rel
     */
    public void setRel(String rel) {
        this.rel = rel;
    }


    @XmlAttribute
    // @ApiModelProperty(value = "Content type hint at link address")
    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Link{");
        sb.append("href='").append(href).append('\'');
        sb.append(", rel='").append(rel).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }


    @Override
    public int hashCode() {
        return Objects.hash(href, rel, type);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Link other = (Link) obj;
        return Objects.equals(this.href, other.href) && Objects.equals(this.rel, other.rel) && Objects.equals(this.type, other.type);
    }
}
