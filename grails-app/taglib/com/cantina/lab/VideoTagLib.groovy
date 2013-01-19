/* Copyright 2006-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information please visit www.cantinaconsulting.com
 * or email info@cantinaconsulting.com
 */
package com.cantina.lab

/**
 * Displays video assets.
 *
 * @author Matt Chisholm
 * @author Adam Stachelek
 * @author Ryan Vanderwerf
 * @author Peter N. Steinmetz
 */
class VideoTagLib {

	static namespace = 'vid'

	private static final String TYPE_FLOWPLAYER = "flowplayer"
	private static final String TYPE_JWFLV = "jwflv"

	def includes = { attrs ->
        def player = attrs.player
        if (player == TYPE_JWFLV) {
		out << """\
            <script type='text/javascript' src='${r.resource(plugin:'gvps',dir:'jw-flv',file:'jwplayer.js')}'></script>
            <script type='text/javascript' src='${r.resource(plugin:'gvps',dir:'jw-flv',file:'swfobject.js')}'></script>

"""
        }
        if (player == TYPE_FLOWPLAYER) {
            out << """\

            <script src='http://releases.flowplayer.org/5.0.0/flowplayer.min.js'></script>
            <link rel='stylesheet' type='text/css' href='http://releases.flowplayer.org/5.0.0/skin/minimalist.css' />
"""

        }

	}

	def display = { attrs ->

		def movieId = attrs.id
		Movie movie = attrs.movie ?: Movie.get(movieId)
		def player = attrs.player
		def stream = attrs.stream


        if (!attrs.width) {
            if (grailsApplication?.config?.video?.player?.width) {
                attrs.width = grailsApplication?.config?.video?.player?.width
            } else {
                attrs.width = '320'
            }

        }
        if (!attrs.height) {
            if (grailsApplication?.config?.video?.player?.height) {
                attrs.height = grailsApplication?.config?.video?.player?.height
            } else {
                attrs.height = '260'
            }

        }

        def meta = grailsApplication.metadata

		def prefix = meta["app.name"]
        if (!prefix) {
            prefix = ""
        }
        if (prefix == '/') {
			prefix = ""
		}
		else {
			if (!prefix.startsWith("/")) {
				prefix = "/" + prefix
			}
		}
		if (pluginContextPath?.length() > 0 && pluginContextPath != '/') {
			prefix += pluginContextPath
		}

		if (player == TYPE_JWFLV) {
			if (movie.status != Movie.STATUS_CONVERTED) {
				out << "<p><b>FLV CONVERSION IN PROGRESS - Please refresh page</b></p>"
				return
			}

			String playerId = "player" + player + movie.id + stream

			if (stream == 'true') {
				out << """\
                    <p id='${playerId}'>
                    <a href='http://www.macromedia.com/go/getflashplayer'>Get Flash</a> to see this player.
                    </p>
                    <script type='text/javascript'>
                    var so = new SWFObject('${r.resource(plugin:'gvps',dir:'jw-flv',file:'player.swf')}','${playerId}',${attrs.width},${attrs.height},'7');
                    so.addVariable('file','${g.createLink(action: 'streamflv', id: movie.id)}');
                    so.addParam('allowfullscreen','true');
                    so.addVariable('streamscript','${g.createLink(action: 'streamflv', id: movie.id)}');
                    so.addVariable('image','${g.createLink(action: 'thumb', id: movie.id)}');
                    so.addVariable('provider','http');
                    so.write('${playerId}');
                   </script>"""
			}
			else {
				out << """\
                    <p id='${playerId}'>
                    <a href='http://www.macromedia.com/go/getflashplayer'>Get Flash</a> to see this player.
                    </p>
                    <script type='text/javascript'>
                    var so = new SWFObject('${r.resource(plugin:'gvps',dir:'jw-flv',file:'player.swf')}','${playerId}',${attrs.width},${attrs.height},'7');
                    so.addParam('allowfullscreen','true');
                    so.addVariable('file','${g.createLink(action: 'display', id: movie.id)}');
                    so.addVariable('image','${g.createLink(action: 'thumb', id: movie.id)}');
                    so.addVariable('provider','http');
                    so.write('${playerId}');
                   </script>"""
			}
		}
		else if (player == TYPE_FLOWPLAYER) {
			if (movie.status != Movie.STATUS_CONVERTED) {
				out << "<p><b>FLV CONVERSION IN PROGRESS - Please refresh page</b></p>"
				return
			}

			String playerId = "player" + player + movie.id + stream

			if (stream == 'true') {
				out << """\
                <object type="application/x-shockwave-flash" data="${r.resource(plugin:'gvps',dir:'flowplay',file:'FlowPlayer.swf')}"
                    width="${attrs.width}" height="${attrs.height}" id="${playerId}">
                    <param name="allowScriptAccess" value="sameDomain" />
                    <param name="movie" value="${r.resource(plugin:'gvps',dir:'flowplayer', file: 'FlowPlayer.swf')}" />
                    <param name="quality" value="high" />
                    <param name="scale" value="noScale" />
                    <param name="wmode" value="transparent" />
                    <param name="flashvars"
                    value="config={streamingServer:'lighttpd',videoFile:'${g.createLink(action: 'streamflv', id: movie.id, controller: 'movie')}',autoPlay:false,showPlayListButtons:true,initialScale:'fit'}" />
                </object>"""
			}
			else {
				out << """\
                <object type="application/x-shockwave-flash" data="${r.resource(plugin:'gvps',dir:'flowplayer',file:'FlowPlayer.swf')}"
                    width="${attrs.width}" height="${attrs.height}" id="${playerId}">
                    <param name="allowScriptAccess" value="sameDomain" />
                    <param name="movie" value="${r.resource(plugin:'gvps',dir:'flowplayer',file:'FlowPlayer.swf')}" />
                    <param name="quality" value="high" />
                    <param name="scale" value="noScale" />
                    <param name="wmode" value="transparent" />
                    <param name="flashvars"
                    value="config={autoPlay:false,showPlayListButtons:true,videoFile:'${g.createLink(action: 'display', id: movie.id)}',initialScale:'fit'}" />
                </object>"""
			}
		}
	}

	def convertVideoPlaytime = { attrs, body ->
		def num = attrs.time

		int minutes = num / 60 //automatically cast to int
		int seconds = num - minutes * 60 //subtract off whole minutes
		int hours = minutes / 60 //automatically cast to int
		minutes = minutes - hours * 60 //subtract off whole hours
		int days = hours / 24 //automatically cast to int
		hours = hours - days * 24 //subtract off whole days

		days > 1 ? out << days + " days " : days > 0 ? out << days + " day " : ""
		hours > 1 ? out << hours + " hours " : hours > 0 ? out << hours + " hour " : ""
		minutes > 1 ? out << minutes + " minutes " : minutes > 0 ? out << minutes + " minute " : ""
		seconds > 1 ? out << seconds + " seconds" : seconds > 0 ? out << seconds + " second" : ""
	}
}
