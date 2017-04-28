package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.mockito.Mockito.verify;

public class MockingTests {

  @Test public void testingMock() {
    List mockedList = new ArrayList();

    mockedList.add("One");
    mockedList.clear();

    verify(mockedList).add("Two");
    verify(mockedList).clear();
  }
}