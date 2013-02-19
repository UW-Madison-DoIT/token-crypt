package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.controller.DownloadController;
import edu.wisc.doit.tcrypt.services.TCryptServices;
import edu.wisc.doit.tcrypt.vo.ServiceKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.io.Reader;
import java.security.PublicKey;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DownloadControllerTest
{
	@InjectMocks
	private DownloadController downloadController;
	@Mock
	private TCryptServices tCryptServices;
	@Mock
	private ServiceKey serviceKey;
	@Mock
	private PublicKey publicKey;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private Reader publicKeyReader;

	@Test
	public void testDownloadAKey() throws Exception
	{
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.getSession().setAttribute("serviceKey_testkey", serviceKey);
		when(serviceKey.getPublicKey()).thenReturn(publicKey);
		downloadController.downloadKey("testkey", "public", request, response);
	}
}
