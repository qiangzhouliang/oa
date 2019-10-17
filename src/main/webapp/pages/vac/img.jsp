<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>

<link rel="stylesheet" href="${ctx}/css/style.css" type="text/css" />
<link rel="stylesheet" href="${ctx}/css/main.css" type="text/css" />


</head>

<body>

	<div id="main">
        
      <div class="sort_switch">
          <ul id="TabsNav">
          	  <li class="selected"><a href="#">流程图 </a></li>
          </ul>
      </div>
      
      <div class="sort_content">
          <img src="${ctx}/generateProcessImageBase64?processInstanceId=${pid}" alt=""/>
      </div>

      
	</div>
    
</body>

</html>
