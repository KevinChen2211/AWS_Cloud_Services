import boto3

from boto3.dynamodb.conditions import Key, Attr
def lambda_handler(event, context):
    type = event['type'];
    email =event['email'];
    web_url =event['web_url']
    # this will create dynamodb resource object and
    # here dynamodb is resource name
    client = boto3.resource('dynamodb')
   
    # this will search for dynamoDB table
    # your table name may be different
    table = client.Table("subscriptions")
   
    if type=="get":
        response = table.scan(FilterExpression=Attr('email').eq(email))

        subscriptions = []
        for item in response['Items']:
            subscriptions.append(item['web_url'])
    
        resp = {
         'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'subscriptions': subscriptions,
            }
        }
    elif type=="options":
        db_value=table.get_item(Key={'email': email})['Item']['web_url']
        resp = {
         'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'subscriptions': db_value,
            }
        }
    elif type=="update":
        response = table.put_item(Item= {'email': email, 'web_url':web_url})

        resp = {
            'statusCode': 200,
            'headers': {
                 'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'response': response
            }
        }
    elif type=="remove":
        table.delete_item(Key={'email': email, 'web_url':web_url})

        resp = {
            'statusCode': 200,
            'headers': {
                 'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'response': 'success'
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
    


