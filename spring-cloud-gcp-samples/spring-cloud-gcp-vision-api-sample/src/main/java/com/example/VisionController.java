/*
 * Copyright 2017-2018 the original author or authors.
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
 */

package com.example;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.common.collect.ImmutableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Code sample that shows how Spring Cloud GCP can be leveraged to use Google Cloud Client
 * Libraries.
 *
 * <p>
 * This uses the Cloud Vision API with the {@link ImageAnnotatorClient}, which is
 * configured and provided by the spring-cloud-gcp-starter-vision module.
 *
 * @author João André Martins
 * @author Daniel Zou
 */
@RestController
public class VisionController {

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private CloudVisionTemplate cloudVisionTemplate;

	/**
	 * This method downloads an image from a URL and sends its contents to the Vision API for label detection.
	 *
	 * @param imageUrl the URL of the image
	 * @return a string with the list of labels and percentage of certainty
	 * @throws Exception if the Vision API call produces an error
	 */
	@GetMapping("/vision")
	public ModelAndView uploadImage(String imageUrl, ModelMap map) throws Exception {
		AnnotateImageResponse response = cloudVisionTemplate.analyzeImage(
				resourceLoader.getResource(imageUrl), Type.LABEL_DETECTION);

		ImmutableMap.Builder<String, Float> annotations = ImmutableMap.builder();
		for (EntityAnnotation annotation : response.getLabelAnnotationsList()) {
			annotations.put(annotation.getDescription(), annotation.getScore());
		}

		map.addAttribute("annotations", annotations.build());
		map.addAttribute("imageUrl", imageUrl);

		return new ModelAndView("result", map);
	}
}
