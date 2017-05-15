/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appengine.gettingstartedjava.helloworld;

import com.google.cloud.vision.spi.v1.ImageAnnotatorClient;
import com.google.cloud.vision.spi.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageSource;
import org.threeten.bp.Duration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// [START example]
@SuppressWarnings("serial")
@WebServlet(name = "helloworld", value = "/")
public class HelloServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter out = resp.getWriter();
    out.println("Hello, world - Flex Servlet");

    List<AnnotateImageRequest> requests = new ArrayList<>();

    ImageAnnotatorSettings.Builder imageAnnotatorSettingsBuilder =
        ImageAnnotatorSettings.defaultBuilder();
    imageAnnotatorSettingsBuilder
        .batchAnnotateImagesSettings()
        .getRetrySettingsBuilder()
        .setTotalTimeout(Duration.ofSeconds(30));
    ImageAnnotatorSettings settings = imageAnnotatorSettingsBuilder.build();

    ImageSource imgSource = ImageSource.newBuilder()
        .setGcsImageUri("gs://today-read/1486612878183portrait3.jpg").build();
    Image img = Image.newBuilder().setSource(imgSource).build();
    Feature feat = Feature.newBuilder().setType(Type.FACE_DETECTION).build();

    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
    requests.add(request);

    ImageAnnotatorClient client = ImageAnnotatorClient.create(settings);
    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
    List<AnnotateImageResponse> responses = response.getResponsesList();

    for (AnnotateImageResponse res : responses) {
      if (res.hasError()) {
        out.printf("Error: %s\n", res.getError().getMessage());
        return;
      }

      // For full list of available annotations, see http://g.co/cloud/vision/docs
      for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
        out.printf(
            "anger: %s\njoy: %s\nsurprise: %s\nposition: %s",
            annotation.getAngerLikelihood(),
            annotation.getJoyLikelihood(),
            annotation.getSurpriseLikelihood(),
            annotation.getBoundingPoly());
      }
    }
  }
}
// [END example]
