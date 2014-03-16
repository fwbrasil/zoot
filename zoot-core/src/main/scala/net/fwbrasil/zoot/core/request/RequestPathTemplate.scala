package net.fwbrasil.zoot.core.request

case class RequestPathTemplate(raw: RequestPath) {

    private val elements =
        raw.elements.map { elem =>
            if (elem.startsWith(":"))
                RequestPathTemplateParam(elem.tail)
            else
                RequestPathTemplateString(elem)
        }

    def tryParse(path: RequestPath) =
        if (matches(path))
            Some(pathParams(path))
        else
            None

    def forParameters(getParam: String => String) =
        RequestPath(elements.map(_.valueForParams(getParam)))

    private def pathParams(path: RequestPath) =
        elements.zip(path.elements).collect {
            case (RequestPathTemplateParam(name), value) =>
                name -> value
        }.toMap

    private def matches(path: RequestPath): Boolean =
        elements.length == path.elements.length &&
            elements.zip(path.elements).forall { tuple =>
                tuple._1.matches(tuple._2)
            }
}

protected sealed trait RequestPathTemplateElement {
    def matches(string: String): Boolean
    def valueForParams(getParam: String => String): String
}

protected case class RequestPathTemplateParam(name: String)
    extends RequestPathTemplateElement {
    def matches(string: String) =
        true
    def valueForParams(getParam: String => String) =
        getParam(name)
}

protected case class RequestPathTemplateString(string: String)
    extends RequestPathTemplateElement {
    def matches(string: String) =
        this.string == string
    def valueForParams(getParam: String => String) =
        string
}
