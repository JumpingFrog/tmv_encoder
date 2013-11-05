#quick script to generate yuv cga palette...

src = [ 0xFF000000,
        0xFF0000AA,
        0xFF00AA00,
        0xFF00AAAA,
        0xFFAA0000,
        0xFFAA00AA,
        0xFFAA5500,
        0xFFAAAAAA,
        0xFF555555,
        0xFF5555FF,
        0xFF55FF55,
        0xFF55FFFF,
        0xFFFF5555,
        0xFFFF55FF,
        0xFFFFFF55,
        0xFFFFFFFF]

def run():
    for i in range(0,16):
        r = (src[i]>>16) & 0xFF
        g = (src[i]>>8) & 0xFF
        b = src[i] & 0xFF

        y = int(r *  .299000 + g *  .587000 + b *  .114000)
        u = int(r * -.168736 + g * -.331264 + b *  .500000 + 128)
        v = int(r *  .500000 + g * -.418688 + b * -.081312 + 128)

        res = ((y&0xFF)<<16) + ((u&0xFF)<<8) + (v&0xFF)
        print hex(res)
        
