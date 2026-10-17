package advanced;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.pd4ml.ResourceProvider;
import com.pd4ml.cache.FileCache;

public class ConvertedImageProvider extends ResourceProvider {

	private int maxwidth = 800;
	private int maxheight = 400;
	
	@Override
	public BufferedInputStream getResourceAsStream(String resource, FileCache cache) throws IOException {
		if (!resource.toLowerCase().startsWith("https:") || !resource.toLowerCase().endsWith(".png")) {
			return null;
		}
		
		// request the standard HTTPS resource loader to load the image bytes
		final com.pd4ml.cache.SslResourceProvider14 provider = new com.pd4ml.cache.SslResourceProvider14();
		final byte[] img = provider.getResourceAsBytes(resource, cache);
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(img));
		final int width = image.getWidth();
		final int height = image.getHeight();
		
		// cannot get image dimensions or image does not exceed the size limit
		if (width <= 0 || height <= 0 && width < maxwidth && height < maxheight) {
			final ByteArrayInputStream bais = new ByteArrayInputStream(img);
			return new BufferedInputStream(bais);
		}

		final double scale = Math.min((double)maxwidth / width, (double)maxheight / height); ;

		final BufferedImage convertedImage = 
				new BufferedImage((int)(width * scale), (int)(height * scale), BufferedImage.TYPE_INT_RGB);
		
		// scale the source image if requested
		if (scale != 1) {
			final AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, scale);
			final AffineTransformOp bilinearScaleOp = 
					new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
			image = bilinearScaleOp.filter(image,
					new BufferedImage((int)(int)(width * scale), (int)(height * scale), image.getType()));
		}
			
		convertedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
		ImageIO.write(convertedImage, "JPEG", baos);

		final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		return new BufferedInputStream(bais);
	}

	@Override
	public boolean canLoad(String resource, FileCache cache) {
		if (resource.toLowerCase().startsWith("https:") && resource.toLowerCase().endsWith(".png")) {
			return true;
		}
		
		return false;
	}

	@Override
	public File localFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeResourceStream() {
		// TODO Auto-generated method stub
		
	}
}