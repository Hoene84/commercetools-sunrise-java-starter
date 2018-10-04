zbarcam | while read line ;
do
	echo $line
	value=${line#*:}
	echo $value

	case "$value" in
	    #redbull
        "90376993")
            chromium-browser http://localhost:9000/en/cart/addArticle/f41384e0-db3f-4915-adfd-1a20e4d99797/1/1
            ;;
        #amarican
        "7630021915712")
            chromium-browser http://localhost:9000/en/cart/addArticle/69f22ebf-a3b6-42da-aff9-48d1ee2cf653/1/1/
            ;;
        #rizzla
        "54034006")
            chromium-browser http://localhost:9000/en/cart/addArticle/f41384e0-db3f-4915-adfd-1a20e4d9/797/1/
            ;;
    esac
done