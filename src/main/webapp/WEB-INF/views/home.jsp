<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ page contentType="text/html; charset=utf8"%>
<%@ page import="java.util.*"%>
<html>
<head>
<title>ホーム｜シアトルライブラリ｜シアトルコンサルティング株式会社</title>
<link href="<c:url value="/resources/css/reset.css" />" rel="stylesheet" type="text/css">
<link href="https://fonts.googleapis.com/css?family=Noto+Sans+JP" rel="stylesheet">
<link href="<c:url value="/resources/css/default.css" />" rel="stylesheet" type="text/css">
<link href="https://use.fontawesome.com/releases/v5.6.1/css/all.css" rel="stylesheet">
<link href="<c:url value="/resources/css/home.css" />" rel="stylesheet" type="text/css">

</head>
<body class="wrapper">
    <header>
        <div class="left">
            <img class="mark" src="resources/img/logo.png" />
            <div class="logo">Seattle Library</div>
        </div>
        <div class="right">
            <ul>
                <li><a href="<%=request.getContextPath()%>/home" class="menu">Home</a></li>
                <li><a href="<%=request.getContextPath()%>/">ログアウト</a></li>
            </ul>
        </div>
    </header>
    <main>
    <div class="f_box">
        <h1>Home</h1>
            <div class="f_item">
                <form method="post" action="searchBook">
                    <div class="search2">
                            <input type="text" class="search1" name="search_title"> 
                            <button type="submit" class="search_btn">検索</button>
                    </div>
                </form>
             </div>
         </div>
         <div class="edtDelBookBtn_box2">
        <a href="<%=request.getContextPath()%>/addBook" class="btn_add_book">書籍の追加</a>
        <a href="<%=request.getContextPath()%>/bulkRegist" class="btn_bulk_book">一括登録</a>
        <a href="<%=request.getContextPath()%>/history" class="btn_history">貸出し履歴一覧</a>
        <%-- <form method="post" action="<%=request.getContextPath()%>/history">
        <button type="submit" class="btn_history">貸出し履歴一覧</button>
        </form> --%>
        </div>
        <div class="content_body">
            <c:if test="${!empty resultMessage}">
                <div class="error_msg">${resultMessage}</div>
            </c:if>
            <div>
                <div class="booklist">
                    <c:forEach var="bookInfo" items="${bookList}">
                        <div class="books">
                            <form method="post" class="book_thumnail" action="<%=request.getContextPath()%>/details">
                                <a href="javascript:void(0)" onclick="this.parentNode.submit();">
                                 	<c:if test="${bookInfo.thumbnail == 'null'}">
                                        <img class="book_noimg" src="resources/img/noImg.png">
                                    </c:if>
                                    <c:if test="${bookInfo.thumbnail != 'null'}">
                                        <img class="book_noimg" src="${bookInfo.thumbnail}">
                                    </c:if>
                                </a> <input type="hidden" name="bookId" value="${bookInfo.bookId}">
                            </form>
                            <ul>
                                <li class="book_title">${bookInfo.title}
                                <li class="book_author">${bookInfo.author}（著）
                                <li class="book_publisher">出版社：${bookInfo.publisher}
                                </li>
                            </ul>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
