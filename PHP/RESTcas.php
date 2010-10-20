<?php
 
# RESTcas
#
# Author Eric Pierce - epierce@usf.edu
# Date April 2009
#
# RESTful CAS client
#
################################ 
#
# Curl, CurlResponse
#
# Author Sean Huber - shuber@huberry.com
# Date May 2008
#
# A basic CURL wrapper for PHP
#
# See the README for documentation/examples or http://php.net/curl for more information about the libcurl extension for PHP
 
class RESTcas
{

   public $ticket_URL;

   protected $error = '';
   protected $curl;
   protected $TGT_location;
   protected $TGT;
   protected $debug;


	public function __construct()
	{
		$this->ticket_URL = '/v1/tickets';
		$this->curl = new Curl;
		$this->debug = false;

	}

	public function setDebug($value = true)
	{
		$this->debug = $value;
	}

	public function CAS_LOGIN($CASurl, $CASuser, $CASpass, $verifySSL = false)
	{
		if ($verifySSL) {
			$this->curl->options['CURLOPT_SSL_VERIFYPEER'] = true;
			$this->curl->options['CURLOPT_SSL_VERIFYHOST'] = true;
			if($this->debug) echo "SSL Certificates WILL be verified. \n";
		} else {
                	$this->curl->options['CURLOPT_SSL_VERIFYPEER'] = false;
                	$this->curl->options['CURLOPT_SSL_VERIFYHOST'] = false;
			if($this->debug) echo "SSL Certificates WILL NOT be verified. \n";
		}

		$this->curl->cookie_file = '/tmp/RESTcas_cookie.txt';

		$post_body = array("username" => "$CASuser","password"=>"$CASpass");

		$response = $this->curl->post($CASurl.$this->ticket_URL.$REST_Location,$post_body);

		if($this->debug){
			echo "=======================\n";
			echo " CAS_LOGIN \n";
			echo "=======================\n";
                        echo "TGT Request: POST URL\n";
                        print_r($CASurl.$this->ticket_URL.$REST_Location);
			echo "\nTGT Request: POST body\n";
                        print_r($post_body);
			echo "TGT Response: Headers\n";
			print_r($response->headers);
			echo "TGT Response: Body\n";
			print_r($response->body);
			echo "\n=======================\n";
		}

		if ($response->headers['Status-Code'] == 201) {
			$this->TGT_Location = $response->headers['Location'];
			return 1;
		}	

		if ($response->headers['Status-Code'] == 400) {
			$this->error = 'Authentication Failure: Username or Password Incorrect';
			if($this->debug) echo "Error: Authentication Failed! \n";
       	         return 0;
       		 }

       		 if ($response->headers['Status-Code'] == 415) {
       		         $this->error = 'Authentication Failure: Unsupported Media Type';
			if($this->debug) echo "Error: Unsupported Media Type! \n";
       		         return 0;
       		 }

    	}



	public function CAS_LOGOUT()
	{

		if (! isset($this->TGT_Location)) {
                        $this->error = 'You must login to CAS first!';
			if($this->debug) echo "Error: No TGT exists! \n";
                        return 0;
                }

		$response = $this->curl->delete($this->TGT_Location);

		if($this->debug){
			echo "\n";
			echo "=======================\n";
			echo " CAS_LOGOUT \n";
			echo "=======================\n";
                        echo "Delete TGT Request: DELETE URL\n";
                        print_r($this->TGT_Location);
			echo "Delete TGT Response: Headers\n";
			print_r($response->headers);
			echo "Delete TGT Response: Body\n";
			print_r($response->body);
			echo "\n=======================\n";
		}
	
		unset($this->TGT_Location);
	
		return 1;


    	}

	function setCookie($cookie_file)
        {
		$this->curl->cookie_file = $cookie_file;
        }

	function getError()
	{
		return $this->error;
	}

	function getTGT()
	{

		if (! isset($this->TGT_Location)) {
			$this->error = 'You must login to CAS first!';
			if($this->debug) echo "Error: No TGT exists! \n";
			return 0;
		} else {
			$this->TGT = substr(strrchr($this->TGT_Location, '/'), 1);
			return $this->TGT;
		}
	}

	function CAS_GET($service)
	{
		if (! isset($this->TGT_Location)) {
                        $this->error = 'You must login to CAS first!';
			if($this->debug) echo "Error: No TGT exists! \n";
                        return 0;
		}

			$response = $this->curl->post($this->TGT_Location,array("service" => $service));
			$this->ST = $response->body;

                if($this->debug){
			echo "\n";
                        echo "=======================\n";
                        echo " CAS_GET: Service Ticket \n";
                        echo "=======================\n";
                        echo "ST Request: POST URL\n";
                        print_r($this->TGT_Location);
                        echo "\nST Request: POST body\n";
                        print_r($service);
                        echo "\nST Response: Headers\n";
                        print_r($response->headers);
                        echo "\nST Response: Body\n";
                        print_r($response->body);
                        echo "\n";
                        echo "=======================\n";
                }

			if (strpos($service,'?') === false) {
				$final_service = $service . '?ticket=' . $this->ST;
			} else {
				$final_service = $service . '&ticket=' . $this->ST;
			}

			$response = $this->curl->get($final_service);

		if($this->debug){
                        echo "\n";
                        echo "=======================\n";
                        echo " CAS_GET: Get Protected Content \n";
                        echo "=======================\n";
                        echo "CAS Protected Service: GET URL\n";
                        print_r($final_service);
                        echo "\nResponse: Headers\n";
                        print_r($response->headers);
                        echo "\nResponse: Body\n";
                        print_r($response->body);
                        echo "\n";
                        echo "=======================\n";
                }

			
                        return $response->body;
	}

	function CAS_POST($service,$vars=array())
        {
                if (! isset($this->TGT_Location)) {
                        $this->error = 'You must login to CAS first!';
			if($this->debug) echo "Error: No TGT exists! \n";
                        return 0;
                }

                        $response = $this->curl->post($this->TGT_Location,array("service" => $service));
                        $this->ST = $response->body;

                if($this->debug){
                        echo "\n";
                        echo "=======================\n";
                        echo " CAS_POST: Service Ticket \n";
                        echo "=======================\n";
                        echo "ST Request: POST URL\n";
                        print_r($this->TGT_Location);
                        echo "\nST Request: POST body\n";
                        print_r($service);
                        echo "\nST Response: Headers\n";
                        print_r($response->headers);
                        echo "\nST Response: Body\n";
                        print_r($response->body);
                        echo "\n";
                        echo "=======================\n";
                }

                        if (strpos($service,'?') === false) {
                                $final_service = $service . '?ticket=' . $this->ST;
                        } else {
                                $final_service = $service . '&ticket=' . $this->ST;
                        }

                        $response = $this->curl->post($final_service, $vars);

                if($this->debug){
			echo "\n";
                        echo "=======================\n";
                        echo " CAS_POST: Get Protected Content \n";
                        echo "=======================\n";
                        echo "CAS Protected Service: POST URL\n";
                        print_r($final_service);
			echo "\nCAS Protected Service: POST vars\n";
			print_r($vars);
                        echo "\nResponse: Headers\n";
                        print_r($response->headers);
                        echo "\nResponse: Body\n";
                        print_r($response->body);
                        echo "\n";
                        echo "=======================\n";
                }


                        return $response->body;
        }

	function CAS_PUT($service,$vars=array())
        {
                if (! isset($this->TGT_Location)) {
                        $this->error = 'You must login to CAS first!';
			if($this->debug) echo "Error: No TGT exists! \n";
                        return 0;
                }

                        $response = $this->curl->post($this->TGT_Location,array("service" => $service));
                        $this->ST = $response->body;

                if($this->debug){
                        echo "\n";
                        echo "=======================\n";
                        echo " CAS_PUT: Service Ticket \n";
                        echo "=======================\n";
                        echo "ST Request: POST URL\n";
                        print_r($this->TGT_Location);
                        echo "\nST Request: POST body\n";
                        print_r($service);
                        echo "\nST Response: Headers\n";
                        print_r($response->headers);
                        echo "\nST Response: Body\n";
                        print_r($response->body);
                        echo "\n";
                        echo "=======================\n";
                }

                        if (strpos($service,'?') === false) {
                                $final_service = $service . '?ticket=' . $this->ST;
                        } else {
                                $final_service = $service . '&ticket=' . $this->ST;
                        }

                        $response = $this->curl->put($final_service, $vars);

                if($this->debug){
			echo "\n";
                        echo "=======================\n";
                        echo " CAS_PUT: Get Protected Content \n";
                        echo "=======================\n";
                        echo "CAS Protected Service: PUT URL\n";
                        print_r($final_service);
			echo "\nCAS Protected Service: PUT vars\n";
			print_r($vars);
                        echo "\nResponse: Headers\n";
                        print_r($response->headers);
                        echo "\nResponse: Body\n";
                        print_r($response->body);
                        echo "\n";
                        echo "=======================\n";
                }


                        return $response->body;
        }

	function CAS_DELETE($service,$vars=array())
        {
                if (! isset($this->TGT_Location)) {
                        $this->error = 'You must login to CAS first!';
			if($this->debug) echo "Error: No TGT exists! \n";
                        return 0;
                }

                        $response = $this->curl->post($this->TGT_Location,array("service" => $service));
                        $this->ST = $response->body;

                if($this->debug){
                        echo "\n";
                        echo "=======================\n";
                        echo " CAS_DELETE: Service Ticket \n";
                        echo "=======================\n";
                        echo "ST Request: POST URL\n";
                        print_r($this->TGT_Location);
                        echo "\nST Request: POST body\n";
                        print_r($service);
                        echo "\nST Response: Headers\n";
                        print_r($response->headers);
                        echo "\nST Response: Body\n";
                        print_r($response->body);
                        echo "\n";
                        echo "=======================\n";
                }

                        if (strpos($service,'?') === false) {
                                $final_service = $service . '?ticket=' . $this->ST;
                        } else {
                                $final_service = $service . '&ticket=' . $this->ST;
                        }

                        $response = $this->curl->delete($final_service, $vars);

                if($this->debug){
			echo "\n";
                        echo "=======================\n";
                        echo " CAS_DELETE: Get Protected Content \n";
                        echo "=======================\n";
                        echo "CAS Protected Service: DELETE URL\n";
                        print_r($final_service);
			echo "\nCAS Protected Service: DELETE vars\n";
			print_r($vars);
                        echo "\nResponse: Headers\n";
                        print_r($response->headers);
                        echo "\nResponse: Body\n";
                        print_r($response->body);
                        echo "\n";
                        echo "=======================\n";
                }


                        return $response->body;
        }



}

//$response = $curl->post($response->headers['Location'],array("service" => $service));
//$ST = $response->body;
//$response = $curl->get($service.'?ticket='.$ST);
//echo $response->body; # A string containing everything in the response except for the headers


class Curl
{
    public $cookie_file;
    public $headers = array();
    public $options = array();
    public $referer = '';
    public $user_agent = '';
 
    protected $error = '';
    protected $handle;
 
 
    public function __construct()
    {
        $this->cookie_file = dirname(__FILE__).'/curl_cookie.txt';
        $this->user_agent = isset($_SERVER['HTTP_USER_AGENT']) ?
            $_SERVER['HTTP_USER_AGENT'] :
            'Curl/PHP ' . PHP_VERSION . ' (http://github.com/shuber/curl/)';
    }
 
    public function delete($url, $vars = array())
    {
        return $this->request('DELETE', $url, $vars);
    }
 
    public function error()
    {
        return $this->error;
    }
 
    public function get($url, $vars = array())
    {
        if (!empty($vars)) {
            $url .= (stripos($url, '?') !== false) ? '&' : '?';
            $url .= http_build_query($vars, '', '&');
        }
        return $this->request('GET', $url);
    }
 
    public function post($url, $vars = array())
    {
        return $this->request('POST', $url, $vars);
    }
 
    public function put($url, $vars = array())
    {
        return $this->request('PUT', $url, $vars);
    }
 
    protected function request($method, $url, $vars = array())
    {
        $this->handle = curl_init();
        
        # Determine the request method and set the correct CURL option
        switch ($method) {
            case 'GET':
                curl_setopt($this->handle, CURLOPT_HTTPGET, true);
                break;
            case 'POST':
                curl_setopt($this->handle, CURLOPT_POST, true);
                break;
            default:
                curl_setopt($this->handle, CURLOPT_CUSTOMREQUEST, $method);
        }
        
        # Set some default CURL options
        curl_setopt($this->handle, CURLOPT_COOKIEFILE, $this->cookie_file);
        curl_setopt($this->handle, CURLOPT_COOKIEJAR, $this->cookie_file);
        curl_setopt($this->handle, CURLOPT_FOLLOWLOCATION, true);
        curl_setopt($this->handle, CURLOPT_HEADER, true);
        curl_setopt($this->handle, CURLOPT_POSTFIELDS, (is_array($vars) ? http_build_query($vars, '', '&') : $vars));
        curl_setopt($this->handle, CURLOPT_REFERER, $this->referer);
        curl_setopt($this->handle, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($this->handle, CURLOPT_URL, $url);
        curl_setopt($this->handle, CURLOPT_USERAGENT, $this->user_agent);
        
        # Format custom headers for this request and set CURL option
        $headers = array();
        foreach ($this->headers as $key => $value) {
            $headers[] = $key.': '.$value;
        }
        curl_setopt($this->handle, CURLOPT_HTTPHEADER, $headers);
        
        # Set any custom CURL options
        foreach ($this->options as $option => $value) {
            curl_setopt($this->handle, constant('CURLOPT_'.str_replace('CURLOPT_', '', strtoupper($option))), $value);
        }
        
        $response = curl_exec($this->handle);
        if ($response) {
            $response = new CurlResponse($response);
        } else {
            $this->error = curl_errno($this->handle).' - '.curl_error($this->handle);
        }
        curl_close($this->handle);
        return $response;
    }
 
}
 
class CurlResponse
{
    public $body = '';
    public $headers = array();
 
    public function __construct($response)
    {
        # Extract headers from response
        $pattern = '#HTTP/\d\.\d.*?$.*?\r\n\r\n#ims';
        preg_match_all($pattern, $response, $matches);
        $headers = split("\r\n", str_replace("\r\n\r\n", '', array_pop($matches[0])));
        
        # Extract the version and status from the first header
        $version_and_status = array_shift($headers);
        preg_match('#HTTP/(\d\.\d)\s(\d\d\d)\s(.*)#', $version_and_status, $matches);
        $this->headers['Http-Version'] = $matches[1];
        $this->headers['Status-Code'] = $matches[2];
        $this->headers['Status'] = $matches[2].' '.$matches[3];
        
        # Convert headers into an associative array
        foreach ($headers as $header) {
            preg_match('#(.*?)\:\s(.*)#', $header, $matches);
            $this->headers[$matches[1]] = $matches[2];
        }
        
        # Remove the headers from the response body
        $this->body = preg_replace($pattern, '', $response);
    }
 
    public function __toString()
    {
        return $this->body;
    }
}
