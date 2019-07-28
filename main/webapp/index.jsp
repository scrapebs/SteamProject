<%-- 
    Document   : index
    Created on : 18.03.2017, 16:23:03
    Author     : denis
--%>

<%@page import="ru.denis.dota2.db.Statistic"%>
<%@page import="static java.lang.Math.abs"%>
<%@page import="com.github.koraktor.steamcondenser.community.SteamId"%>
<%@page import="ru.denis.dota2.db.ConnectDB"%>
<%@page import="ru.denis.dota2.db.lastPlayers"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<% if(request.getParameter("ref") != null)
    request.getSession().setAttribute("ref", request.getParameter("ref"));
%>

<!doctype html>
<html>
<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
<script>
     $(window).load(function() {
        $("#loading").fadeOut(500);
     });
</script>
<head>
    <meta charset="utf-8">
    <style>
       <%@include file='/css/style.css' %>
    </style>
    <title>DOTARATE</title>
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
                    <div class="header_left" >
				<div class = "logo">
					<a href="#"><img src="data:image/png"></a>
				</div>
                    </div>
                            
                    <div class = "header_right_index">
                        <div class = "menu_index">
                          <ul>
                            <li><a style="font-size:19px;" href="/trade">Играть</a></li>
                            <li><a style="font-size:19px;" href="/trade">Депозит</a></li>
                            <li><a style="font-size:19px;" href="/trade">Вывести</a></li>
                            <li><a style="font-size:19px;" href="/trade">Рефералы</a></li>
                            <li class="inter"><a href="/trade"><img style="padding-right:70px; padding-top: 9px"  src ="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB8AAAAPCAYAAAAceBSiAAACP0lEQVR42r3UP2hTURTH8VdjlAwiFlERB6fe+xCd6uCfQXEoqIvQLuomCoqDotaqpPivWorU0oo0TdMmpolp0kxt0UFFB3FxkWjboVoRi3YQOliJf/pOvy9cIURTQm08672XD797Dseyylh2T/9KO9zfYkeSE3Y0NWPHUm/s+EC9nUh7rbLC3YnV4CPgP8H7wK+AD4IL+GM7mV6Wu6jOBtap+kCTauiKqAvBU+pScMUi4CHwb+A7wb3gG8ErwGvBZ8EbraozndXgo+AnwPeDXwN/pfzdqxYMd91fCv4VvAPcB54BF/Dh3HkiPQA+7uJPwXeDx8AFfBL8LfjNf8C3ggv4cfBqcDG4kHwteBN41sU/g+8AF4MLuIA7qjH0SF0OHVJXe3ylwroz7gPPGDwI7gV/YvCQST4EPubi4+C7wJ0CXMAFXMCn1fXegL4R3lYC3gEuBv8OvicHRlO5ACQ/DO6An3PxY+APwRvA3xfBBVzARTeHR3VL5Ly+dW/9H/Dd+D5wJw8X8FmSD4K3kfyZmXY3uef3tB8BHwP/CP4CPAWeLYILuID/0q3RYd0WrdPtfQf1nVg7+BS4FOBiev4F/Dn4UXDPvN8HXgl+EvxlEVzABVzABXwE/Db4VAHugLeCL1/Q9IJvAm8Gn5wHF/Bp8AN5+CfwmkXZWuAe8L3gSfDsX3ABrwP/AD4EvqYs6xO8ErwX/EcePgO+2eBLyrq/wTeAZ8D94H7w7eAx8IvW/yjwLeAPwN+BvwY/DV5Ryts5ITDbZTt7wiAAAAAASUVORK5CYII="><span style="padding-left:42px; font-size:16px; color:#fff;">ВОЙТИ</span></a></li>
                           </ul>
                        </div>
                    </div>
                </center>
		</header>
		
		<div class = "content">
			<div class = "regestration">
                <% Statistic stat = ConnectDB.getStatistic(); %>
                    <table class="tb" width="1260px" border="0" bgcolor = "#131c27" style="  border-radius: 4px 4px 4px 4px">
                        <tbody>
                            <tr>
                                <td style="text-align:center; color:#969c9c; font-size: 30px;  border-right: 0.5px solid #505861;" width ="403px" height="100" > <%= stat.players%> <br>
                                     <span style="font-size:19px;"><strong>игроков зарегистрировано</strong></span></td>
                                <td style="text-align:center; color:#969c9c; font-size: 30px; border-right: 0.5px solid #505861;" width ="452px">  <%= stat.matches%> <br> <span style="font-size:19px;"><b>матчей сыграно</b></span></td>
                                <td style="text-align:center; color:#969c9c; font-size: 30px;" width ="405px">$<%= stat.money %> <br><span style="font-size:19px;"><b> денег выплачено</b></span></td>
                            </tr>
                        </tbody>
                    </table>

                    </div> <br><br>
                        <div class="center_content">
                            <table class="center" width="1260px" border="0" bgcolor = "#131c27"  style="border-radius: 4px 4px 4px 4px ">
                                <tbody>
                                    <tr>
                                        <td  width="400px" style=" border-right: 0.1px solid #505861; text-align:center;">        
                                            <table width="400" border="0" bgcolor = "#131c27" >  
                                                <tbody>
                                                  <tr>
                                                      <td style="font-weight:bold; text-align: center; vertical-align: top; padding-top: 27px;  color: #cccfcf;">Последние матчи<hr color="#696b6b" size="1" width="80%"></td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                        <table  border="0">
                                                          <tbody>
                                                             <%ArrayList<lastPlayers> players = ConnectDB.lastPlayers();
                                                              lastPlayers temp_player;
                                                              if(players.size()==5){
                                                              for (int i=0; i<5; i++) {
                                                                  temp_player = players.get(i);
                                                                  String avatarplayer;
                                                                  String nameplayer ="Dotaplayer";
                                                                  try{
                                                                      SteamId profile = SteamId.create(Long.parseLong(temp_player.iduser));
                                                                      nameplayer = profile.getNickname();
                                                                      avatarplayer = profile.getAvatarMediumUrl();
                                                                  }
                                                                  catch(Exception ex){
                                                                      avatarplayer = "";
                                                                  }
                                                              %>
                                                              <tr>
                                                                 <td width = "62"  ><a href="http://steamcommunity.com/profiles/<%=temp_player.iduser%>" target="_blank" style="vertical-align: top;"> <img style="margin-left:85px" width="50px" class="round" src=<%=avatarplayer%>> </a> </td>
                                                                 <td align="left"><a href="http://steamcommunity.com/profiles/<%=temp_player.iduser%>" target="_blank"  style="text-decoration: none; color:#696b6b;"><span style=" color:#cccfcf; font-size: 18px"><%=nameplayer%> </span></a><br> <span style=" font-size: 17px">
                                                                  <% if(temp_player.rate>=0){ %>Выиграл   </span>  <span style="color:#56c456; font-size: 17px"><%=temp_player.rate%>$</span><%} else {%> Проиграл </span>  <span style="color:#cccfcf; font-size: 17px"><%= abs(temp_player.rate)%>$</span> <% }%></td>
                                                              </tr>
                                                                 <% } 
                                                               }%>
                                                          </tbody>
                                                         </table>
                                                     </td>
                                                  </tr>

                                                 </tbody>
                                            </table>
                                        </td>
                                        <td width="460px" style="text-align:center; vertical-align: text-top;  border-right: 0.1px solid #505861;">  
                                            <table  width="447" height="350" border="0" bgcolor = "#131c27"  style=" ">
                                                <tbody>
                                                  <tr>
                                                    <td style="font-weight:bold; text-align:center; vertical-align: text-top; padding-top: 27px;  color: #cccfcf;">  DOTARATE <hr color="696b6b" size="1" width="80%"></td>
                                                  </tr>
                                                  <tr>
                                                      <td><p align="center" style="line-height:2; font-size:23px; padding-bottom: 27px; margin-left:32px; margin-right:32px;  color: #cccfcf;" >Приветствуем вас на проекте<br>DOTARATE. Ставьте на свою <br>победу и выводите<br>крутые скины!</p></td>
                                                  </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                        <td width="400px" style="vertical-align: text-top; vertical-align: top; text-align:center;">
                                            <table width="400" height="350" border="0"   bgcolor = "#131c27">
                                                <tbody>
                                                  <tr>
                                                    <td style="font-weight:bold; text-align:center; vertical-align: text-top; padding-top: 27px;  color: #cccfcf;  " >Правила <hr color="696b6b" size="1" width="85%"></td>
                                                  </tr>
                                                  <tr>
                                                    <td style="text-align:center;   color: #cccfcf;  vertical-align: top;">
                                                  <iframe width="364" height="250"  src="https://www.youtube.com/embed/YzbJMCSfcPY" frameborder="0" allowfullscreen></iframe>  </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>          
                </div>
	</body>
</html>
