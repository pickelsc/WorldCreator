#version 120

varying vec2 texCoordV;

uniform sampler2D texUnit1;
//uniform sampler2D texUnit2;

varying vec3 N;
varying vec4 v;

/* We are only taking into consideration light0 and assuming it is a point light */
void main (void) {	
   vec4 ambient, globalAmbient;
    
    /* Compute the ambient and globalAmbient terms */
	ambient =  gl_LightSource[1].ambient * gl_FrontMaterial.ambient;
	globalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;

	/* Diffuse calculations */
	vec3 normal, lightDir; 
	
	vec4 diffuse;
	float NdotL;
	
	/* normal has been interpolated and may no longer be unit length so we need to normalise*/
	normal = normalize(N);
	
	/* normalize the light's direction. */
	lightDir = normalize(vec3(gl_LightSource[1].position.xyz - vec3(v)));
	//lightDir = normalize(vec3(-gl_LightSource[0].position.xyz));
    NdotL = max(dot(normal, lightDir), 0.0); 
    
    /* Compute the diffuse term */
    diffuse = NdotL * gl_FrontMaterial.diffuse * gl_LightSource[1].diffuse; 

    vec4 specular = vec4(0.0,0.0,0.0,1);
    float NdotHV;
    float NdotR;
    vec3 dirToView = normalize(vec3(-v));
    
    vec3 R = normalize(reflect(-lightDir,normal)); 
    vec3 H =  normalize(lightDir+dirToView); 
   
    /* compute the specular term if NdotL is larger than zero */
    
	if (NdotL > 0.0) {
		NdotR = max(dot(R,dirToView ),0.0);
		
		//Can use the halfVector instead of the reflection vector if you wish 
		NdotHV = max(dot(normal, H),0.0);
		
		specular = gl_FrontMaterial.specular * gl_LightSource[1].specular * pow(NdotHV,gl_FrontMaterial.shininess);
	    
	}
	
	specular = clamp(specular,0,1);


	vec4 texture = texture2D(texUnit1,texCoordV);
	vec4 lighting = ambient + diffuse + specular;
	
	gl_FragColor = globalAmbient + lighting * texture;

    //gl_FragColor = globalAmbient + ambient + diffuse + specular;
    //gl_FragColor = mix(texture2D(texUnit1,texCoordV),texture2D(texUnit2,texCoordV),0.25);
	//gl_FragColor = texture2D(texUnit1,texCoordV);
}

