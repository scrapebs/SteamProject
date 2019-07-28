<%-- 
    Document   : test
    Created on : 26.03.2017, 14:36:31
    Author     : denis
--%>

<%@page import="ru.denis.dota2.db.InventoryShort"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.net.MalformedURLException"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="com.github.koraktor.steamcondenser.community.dota2.Dota2Inventory"%>
<%@page import="com.github.koraktor.steamcondenser.community.WebApi"%>
<%@page import="com.github.koraktor.steamcondenser.community.SteamId"%>
<%@page import="ru.denis.dota2.servlets.InventoryServlet"%>
<%@page import="ru.denis.dota2.util.Inventory"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.denis.dota2.db.Inventory"%>
<%@page import="ru.denis.dota2.db.ConnectDB"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ru.denis.dota2.util.Util"%>

<style>
   <%@include file='/css/style.css' %>
   <%@include file='/css/style_button_green.css' %>
   <%@include file='/css/style_button_simple.css' %>
   <%@include file='/css/style_check.css' %>
</style>
 

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>DOTARATE</title>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
    <script>
        $(window).load(function() {
            $("#loading").fadeOut(500);
        });
    </script>

    <%
    if(request.getSession().getAttribute("steamid") == null){
        response.sendRedirect(Util.getFullUrl(request, "/"));
        return;
    }

    String id = (String)request.getSession().getAttribute("steamid");
    float count = ConnectDB.getCount(id);

    // Inventory of user
    ArrayList<Inventory> resaultinventory = InventoryServlet.getInventoryItems(id);

    SteamId profile = SteamId.create(Long.parseLong(id));  // для аватарки, никнейма

    String quer = "";

    String select[] = request.getParameterValues("checkclassid");
    if ( select != null ) {
        // if(select.length>8)
        quer = ConnectDB.CreateQueryDepozit(id, select);
        response.sendRedirect(Util.getFullUrl(request, "/inventory"));
    }

    String tradelink = ConnectDB.getTradeLink(id);
    boolean readytotrade = false;
    if(!tradelink.equals("") && !profile.isBanned() )
        readytotrade = true;
    /*  !profile.getPrivacyState() profile.isLimitedAccount())*/
    String result="";
    if(request.getParameter("tradelink") != null){
        // проверка на корректный ввод
        result = ConnectDB.CreateTradeLink(id, (String)request.getParameter("tradelink"));
        response.sendRedirect(Util.getFullUrl(request, "/inventory"));
    }
    %>

    <script>
    // Открываю попап
    $(document).ready(function() {
        $('a#start').click( function(event){
            event.preventDefault();
            $('#overlay').fadeIn(250,
                function(){
                    $('#popUp')
                        .css('display', 'block')
                        .animate({opacity: 1, top: '55%'}, 490);
            });
        });
    // По нажатию на крестик закрываю окно
        $('#close, #overlay').click( function(){
            $('#popUp')
                .animate({opacity: 0, top: '35%'}, 490,
                    function(){
                        $(this).css('display', 'none');
                        $('#overlay').fadeOut(220);
                    }
                );
        });
    });
    window.ass=1;
    </script>

    <style>
    #popUp {
            top: 30%;
        left: 50%;
        height: 200px;
        position: fixed;
        width: 300px;
        border-radius: 11px;
        background: #232a33;
        margin-left: -150px;
        margin-top: -100px;
        display: none;
        opacity: 0;
        padding: 17px;
        z-index: 6;
    }
    #popUp #close {
    cursor: pointer;
        position: absolute;
        width: 23px;
        height: 23px;
        top: 180px;
        right: 180px;
        display: block;
    }
    #overlay {
        z-index:1;
        background-color:#42444B;
        position:fixed;
        opacity:0.86;
        width:100%;
        height:100%;
        display:none;
        top:0;
        left:0;
    }
    </style>

    <script>
    // Открываю ПРАВИЛА
    $(document).ready(function() {
        $('a#startRules').click( function(event){
            event.preventDefault();
            $('#overlay').fadeIn(250,
                function(){
                    $('#popUpRules')
                        .css('display', 'block')
                        .animate({opacity: 1, top: '55%'}, 490);
            });
        });
    // По нажатию на крестик закрываю окно
        $('#close, #overlay').click( function(){
            $('#popUpRules')
                .animate({opacity: 0, top: '35%'}, 490,
                    function(){
                        $(this).css('display', 'none');
                        $('#overlay').fadeOut(220);
                    }
                );
        });
    });
    window.ass=1;
    </script>
    <style>
    #popUpRules {
            top: 30%;
        left: 50%;
        height: 200px;
        position: fixed;
        width: 300px;
        border-radius: 11px;
        background: #232a33;
        margin-left: -150px;
        margin-top: -100px;
        display: none;
        opacity: 0;
        padding: 17px;
        z-index: 6;
    }
    #popUpRules #close {
    cursor: pointer;
        position: absolute;
        width: 23px;
        height: 23px;
        top: 180px;
        right: 180px;
        display: block;
    }
    #overlay {
        z-index:1;
        background-color:#42444B;
        position:fixed;
        opacity:0.86;
        width:100%;
        height:100%;
        display:none;
        top:0;
        left:0;
    }
    </style>

    <style>
          #zatemnenie {
            background: rgba(102, 102, 102, 0.5);
            width: 100%;
            height: 100%;
            position: absolute;
            top: 0;
            left: 0;
            display: none;
          }
          #okno {
            width: 300px;
            height: 50px;
            text-align: center;
            padding: 15px;
            border: 3px solid #0000cc;
            border-radius: 10px;
            color: #0000cc;
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            margin: auto;
            background: #fff;
          }
          #zatemnenie:target {display: block;}
          .close {
            display: inline-block;
            border: 1px solid #0000cc;
            color: #0000cc;
            padding: 0 12px;
            margin: 10px;
            text-decoration: none;
            background: #f2f2f2;
            font-size: 14pt;
            cursor:pointer;
          }
          .close:hover {background: #e6e6ff;}
    </style>

    <script>
    /*открываю ссылку*/
    $(document).ready(function() {
        $('a#startLink').click( function(event){
            event.preventDefault();
            $('#overlay').fadeIn(250,
                function(){
                    $('#popUpLink')
                        .css('display', 'block')
                        .animate({opacity: 1, top: '55%'}, 490);
            });
        });
    /*по нажатию на крестик закрываю окно*/
        $('#close, #overlay').click( function(){
            $('#popUpLink')
                .animate({opacity: 0, top: '35%'}, 490,
                    function(){
                        $(this).css('display', 'none');
                        $('#overlay').fadeOut(220);
                    }
                );
        });
    });
    window.ass=1;
    </script>
    <style>
    #popUpLink {
            top: 30%;
        left: 40%;
        height: 200px;
        position: fixed;
        width: 580px;
        border-radius: 6px;
        background: #232a33;
            color:#969c9c;
            font-size: 13px;
        margin: 0 auto;
        margin-top: -100px;
        display: none;
        opacity: 0;
        padding: 17px;
        z-index: 6;
    }
    #popUpLink #close {
    cursor: pointer;
        position: absolute;
        width: 23px;
        height: 23px;
        top: 180px;
        right: 180px;
        display: block;
    }
    #overlay {
        z-index:1;
        background-color:#42444B;
        position:fixed;
        opacity:0.86;
        width:100%;
        height:100%;
        display:none;
        top:0;
        left:0;
    }
    </style>

	<script>
	/*	new Ajax.Request( 'http://localhost:8080/RefreshServlet', {
			method: 'post',
			onSuccess: function() {
				alert("post request");
			}
		} );*/
	</script>

</head>
<body>
    <header>
        <div id="loading">
            <div id="loading-center">
                <div id="loading-center-absolute">
                    <div id="object"></div>
                </div>
            </div>

        </div>
   
        <center>
            <div class="header_left">
                        <div class = "logo">
                <a href="/"><img src=""></a>
                        </div>
            </div>
        </center>
        <div class = "header_right">
            <div class = "menu">
                 <ul>
                    <li><a style="font-size:19px;" href="/">Играть</a></li>
                    <li><a style="font-size:19px;" href="/inventory">Депозит</a></li>
                    <li><a style="font-size:19px;" href="/withdraw">Вывести</a></li>
                    <li><a style="font-size:19px;" href="/referal">Рефералы</a></li>
                    <li><a style="font-size:19px;" href="#" id="startRules">Правила</a></li>
                    <li class="nic" style="background-color:#171a21; margin-top: -45px; width:100px;"><a href="#"><img style="width: 50px; height:auto ; margin-top: 10px; position:absolute;" class="round" src="<%= profile.getAvatarMediumUrl()%>"><p style="padding-top: 12px; margin-left: 52px; color: #a3b6a1; font-size:14px; text-shadow: 0px 0px 0px rgba(0,0,0,.7);"><%=profile.getNickname()%></p><br><p style="margin-top: -30px; color: #a3b6a1; font-size:14px; margin-left: 52px; color: #a3b6a1; text-shadow: 0px 0px 0px rgba(0,0,0,.7);">$<%=count%></p></a>
                    <div class="spis"><ul>
                    	<li>
                            <p style="color:#C7C7C7; margin-left: -5%; margin-top: 0px"><a href="#" id="startLink">Cсылка для обмена</a></p>
                    	</li>
                        <hr color="#696b6b" size="2" width="100%" style="margin-left: -20px;">
                        <li><a href="/logout">Выход</a></li>
                        </ul>
                    </div>
                    </li>
                </ul>
            </div>
        </div>
           
    </header>

    <div class = "content">
        <center>  
        <form ACTION="inventory.jsp">             
        <div class = "regestration">
            <table width="1200px" border-style ="none" border ="0" bgcolor = "#131c27" style="  border-radius: 4px 4px 4px 4px">
                <tbody>
                    <tr>
                                  <td style="text-align:center; color:#969c9c; font-size: 18px; padding-top: 2px; padding-bottom:18px; border-right: 2px solid #505861;">На Вашем счету:  <%= count %>$<br>
                                   Выберете вещи, которые хотите обменять на дотапоинты, и нажмите «Внести»</td>
                                  <% if (readytotrade) { %>
                                  <td> <input type="submit" class="green" name="dowithdraw" value="Внести"  size="1" style="padding-left:6px; margin-left:55px; width : 200px; font-size:16px; background-color: #232a33; color:#F8F8F8;"/></td>
                                  <% } else { %>
                                  <td> <a href="#" id="startLink"> <button class="green" name="button1"   style="padding-left:6px; margin-left:55px; width : 200px; font-size:16px; background-color: #232a33; color:#F8F8F8;"> Внести</button> </a></td>
                                  <% } %>
                                </tr>
                              </tbody>
                            </table><br>
	</div>   
     
                 <table width="1200px"  border-style ="none" border ="0"  bgcolor = "#16202d" style="border-radius: 4px 4px 4px 4px; border-left: 87px solid #16202d; border-right: 88px solid #16202d; border-bottom: 70px solid #16202d;  padding-top: 5px;  border-spacing: 25px; ">
                          <tbody>

            <% 

            
                
           // ArrayList<Inventory> resaultinventory = InventoryServlet.getInventoryItems(id);
              
            Inventory temp;
            
            
                  for (int i=0; i<resaultinventory.size(); i+=6) {
                       out.println("<tr>");
                     //   out.println(tt);
               for (int j=0; j<6; j++) {
                   if(i+j < resaultinventory.size()){
                       temp =resaultinventory.get(i+j);
                     %>
                         
                                 <td style="width: 20px; height: 116px; background-color: #16202d; color:#F8F8F8; " width="140">  
                                         <img src="http://steamcommunity-a.akamaihd.net/economy/image/<% out.println(temp.imageitem); %> " width="140px" style="margin-bottom: 2px; margin-left: auto; margin-right: auto;"><br> 
                                     
                                       <label>
                                             <input type="checkbox" name="checkclassid" value="<%=temp.pricetotake%>/<%=temp.iditem%>"> <span style="padding-top: 4px;  ">   $<% out.println(temp.pricetotake);%></span>
                                       </label>
                                       
                                 </td>
                               
                         <%  }
                                    
                    }
                    out.println(" </tr>");
                    
                }
                %>
            </tbody>
            </table>
        </center>    
        </form>    
        </div>    
            
            <div id="popUp"><p style="color:#F8F8F8;">Вы уже сделали запрос на внесение предмета. Зайдите в личный кабинет Steam и примите обмен.</p>
                    <p style="color:#F8F8F8;"><span id="close"><button  type = "button" class="simple" name = "button1" style = "width: 80px; height:40px; color:#F8F8F8; background-color: #2e3740;">OK</button></span></p>
            </div>
            <div id="overlay"></div>   
    
             <div id="popUpRules"><p style="color:#F8F8F8; text-shadow: 1px -1px 1px rgba(0,0,0,.7); font-size: 14px">Выберете вещи, которые хотите обменять на дотапоинты, и нажмите «Внести». <br>Бот отправит вам предложение обмена. Примите обмен в личном кабинете Steam. <br> Убедитесь, что вы правильно указали ссылку для обмена, и ваш инвентарь публичный.</p>
                    <p style="color:#F8F8F8;"><span id="close"><button  type = "button"  class="simple" name = "button1" style = "width: 80px; height:40px; color:#F8F8F8; background-color: #2e3740;">OK</button></span></p>
             </div>
             <form action="" method="POST">
                <div id="popUpLink"><p style="color:#F8F8F8; font-size:15px">Ваша ссылка для обмена</p><br>
                    Пожалуйста введите Вашу ссылку для обмена. <a href="http://steamcommunity.com/profiles/<%=id%>/tradeoffers/privacy#trade_offer_access_url" target="_blank" style="color: #f2f2f2">Где ее получить?</a><br>
                    <input type="text" name="tradelink" id="tradelink" value="<%= tradelink %>" size="20"  placeholder="https://steamcommunity.com/tradeoffer/new/?partner=123446462&token=j0b456Hu" autocomplete="off" style=" height: 30px; width: 580px;  border-radius: 5px;  border: 1px solid #666; font-size:13px;background-color: #1d242d; color:#F8F8F8;"/> <br><br>
                    Убедитесь, что Ваш <a href="https://steamcommunity.com/profiles/<%=id%>/edit/settings" target="_blank" style="color: #f2f2f2"> инвентарь публичный.</a> <br>
                    <p style="color:#F8F8F8;"><span id="close"><button  type = "button" class="simple" name = "button1" style = "width: 80px; height:40px; color:#F8F8F8; ">Отмена</button></span>
                    <input type="submit" class="simple" name="savetradelink"   value="Сохранить" size="1" /> </p>
                </div>
            </form>

    </body>
</html>
