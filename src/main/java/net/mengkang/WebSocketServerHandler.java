package net.mengkang;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import net.mengkang.entity.Client;
import net.mengkang.manager.ClientMgr;
import net.mengkang.manager.MessMgr;
import net.mengkang.service.RequestService;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.HOST;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    // websocket 服务的 uri
    private static final String WEBSOCKET_PATH = "/websocket";

    // 本次请求的 code
    private static final String HTTP_REQUEST_STRING = "request";

    private Client client;

    private WebSocketServerHandshaker handshaker;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // Handle a bad request.
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        // Allow only GET methods.
        if (req.method() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        if ("/favicon.ico".equals(req.uri()) || ("/".equals(req.uri()))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
        Map<String, List<String>> parameters = queryStringDecoder.parameters();

        if (parameters.size() == 0 || !parameters.containsKey(HTTP_REQUEST_STRING)) {
            System.err.printf(HTTP_REQUEST_STRING + "参数不可缺省");
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }
        // 请求字符串
        String requestString = parameters.get(HTTP_REQUEST_STRING).get(0);
        client = RequestService.clientRegisterChannel(ctx.channel(),requestString);
        if(client == null){
            System.err.printf("登录协议有错误");
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }
        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            ChannelFuture channelFuture = handshaker.handshake(ctx.channel(), req);

            // 握手成功之后,业务逻辑
            if (channelFuture.isSuccess()) {
                if (client.getClientId() == 0) {
                    System.out.println(ctx.channel() + " 游客");
                    return;
                }

            }
        }
    }
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        String request = ((TextWebSocketFrame) frame).text();
        MessMgr.distribution(ctx.channel(),request);
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaderUtil.setContentLength(res, res.content().readableBytes());
        }

        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaderUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("收到" + incoming.remoteAddress() + " 握手请求");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (client != null && ClientMgr.isHasRoom(client.getRoomId())) {
            List<Client> allRoomClient =  ClientMgr.createRoom(client.getRoomId());
            allRoomClient.remove(client);
        }
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }
}
