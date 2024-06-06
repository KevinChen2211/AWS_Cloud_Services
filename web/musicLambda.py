import boto3
from boto3.dynamodb.conditions import Key, Attr

def lambda_handler(event, context):
    type = event['type'];
    title =event['title'];
    web_url =event['web_url']
    year =event['year']
    artist =event['artist']

    client = boto3.resource('dynamodb')
   
    table = client.Table("music")
   
    if type=="getDetails":
        title_value=table.get_item(Key={'web_url': web_url})['Item']['title']
        year_value=table.get_item(Key={'web_url': web_url})['Item']['year']
        artist_value=table.get_item(Key={'web_url': web_url})['Item']['artist']
        
        resp = {
         'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'title': title_value,
                'year': year_value,
                'artist': artist_value
            }
        }
    elif type=="options":
        title_value=table.get_item(Key={'web_url': web_url})['Item']['title']
        year_value=table.get_item(Key={'web_url': web_url})['Item']['year']
        artist_value=table.get_item(Key={'web_url': web_url})['Item']['artist']
        
        resp = {
         'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'title': title_value,
                'year': year_value,
                'artist': artist_value
            }
        }
    elif type=="search":
        
        search_expression = Attr('title').contains(title) & Attr('year').contains(year) & Attr('artist').contains(artist)

        filtered_items = table.scan(FilterExpression=search_expression)
        
        search = list(filtered_items['Items']) 
        
        resp = {
         'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'search': list(search)
            }
        }
    else:
        resp = {
            'statusCode': 404,
            'headers': {
                 'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'error': 'type not found (e.g. post/get)'
            }
        }
    return resp
    


