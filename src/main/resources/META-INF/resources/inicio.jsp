<%@ include file="./init.jsp"%>

<c:choose>
  <c:when test="${fn:contains(urlCurrent, 'pago-con-tarjeta')}">
    <%@ include file="./view.jsp" %>
  </c:when>
  <c:when test="${fn:contains(urlCurrent, 'comprobante')}">
    <%@ include file="./comprobante.jsp" %>
  </c:when>
  <c:otherwise>
    <h1>Not Found</h1>
  </c:otherwise>
</c:choose>

