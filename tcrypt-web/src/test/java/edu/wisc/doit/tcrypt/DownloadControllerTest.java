package edu.wisc.doit.tcrypt;

import edu.wisc.doit.tcrypt.dao.IKeysKeeper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DownloadControllerTest {
	@InjectMocks private DownloadController downloadController;
	@Mock private IKeysKeeper tcryptHelper;
	
	@Test
	public void testDownloadAKey () throws Exception {
/*
		when(tcryptHelper.getKeyLocationToDownloadFromServer("test","test","private")).thenReturn("test");
		//TODO : Adjust test case when API is in place.
*/
		//downloadController.downloadKey("test", "test", Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class));
		assertTrue(true);
	}
}
