<%--
  Created by IntelliJ IDEA.
  User: 小山勇
  Date: 2018/11/6
  Time: 15:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
    <script src='js/jquery-1.7.2.min.js'></script>
</head>
<body>
    <div class="Sbox">
        <c:forEach items="${skuList}" var="sku">
            <div class="list">
                <div class="img"><img src="images/img_4.jpg" alt=""></div>
                <div class="price">¥${sku.jg}</div>
                <div class="title">
                    <a target="_blank" href="toItemPage.do?skuId=${sku.id}&spuId=${sku.shpId}">${sku.skuMch}</a>
                </div>
            </div>
        </c:forEach>
    </div>
</body>
</html>
